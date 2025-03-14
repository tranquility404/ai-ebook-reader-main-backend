package com.tranquility.ebookreader.controller;

import com.tranquility.ebookreader.dto.*;
import com.tranquility.ebookreader.model.*;
import com.tranquility.ebookreader.model.ResponseStatus;
import com.tranquility.ebookreader.service.*;
import com.tranquility.ebookreader.utils.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final EbookAnalysis ebookAnalysis;
    private final GCloudService gCloudService;

    private final ReadHistoryService readHistoryService;
    private final TestService testService;

//    private final Map<String, Sinks.Many<JobStatus>> statuses = new ConcurrentHashMap<>();
    private final Map<String, AddBookRequest> addBookRequests = new ConcurrentHashMap<>();

    public BookController(BookService bookService, EbookAnalysis ebookAnalysis,
                          GCloudService gCloudService, ReadHistoryService readHistoryService,
                          TestService testService) {
        this.bookService = bookService;
        this.ebookAnalysis = ebookAnalysis;
        this.gCloudService = gCloudService;

        this.readHistoryService = readHistoryService;
        this.testService = testService;
    }

    @PostMapping("/add-book")
    public ResponseEntity<?> addBook(@RequestParam("file") MultipartFile file) {
        try {
            final String requestId = UUID.randomUUID().toString(); // unique ID for tracking this request
            ebookAnalysis.init(file.getInputStream());
            ebookAnalysis.loadBookInfo();
            ebookAnalysis.loadChaptersLite();

            BookInfo info = ebookAnalysis.getBookInfo();
            String userId = AuthUtils.getUsername();
            info.setPostedBy(userId);

            BookInfo duplicate = bookService.findByGpbkIdAndPostedBy(info.getGpbkId(), userId);
            if (duplicate != null && info.equals(duplicate))
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Can't upload same file again"));

            addBookRequests.put(requestId, new AddBookRequest(SerializationUtils.clone(info), file.getBytes()));
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> addBookRequests.remove(requestId), 15, TimeUnit.MINUTES);
            scheduler.shutdown();

            BookInfoDto bookInfoDto = bookService.convertToDto(info);

            return ResponseEntity.ok(Map.of("requestId", requestId, "response", bookInfoDto));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/save-book/{requestId}")
    public ResponseEntity<?> confirmData(@PathVariable String requestId) {
        AddBookRequest addBookRequest = addBookRequests.get(requestId);
        String userId = AuthUtils.getUsername();
        try {
            if (addBookRequest != null && userId != null) {
                String cloudUrl = gCloudService.uploadBookFile(requestId, addBookRequest.getFile());

                BookInfo info = addBookRequest.getBookInfo();
                info.setCloudUrl(cloudUrl);
                info.setPostedBy(userId);
                for (var i : info.getChapters())
                    i.setTexts(null);
                bookService.addBook(addBookRequest.getBookInfo());

                return ResponseEntity.ok("Book saved!");
            } else
                return ResponseEntity.ok("Book failed to save!");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while saving: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/my-collection")
    public ResponseEntity<?> getMyCollection() {
        List<BookInfoDto> myBooks = bookService.getMyCollection(AuthUtils.getUsername());
        return ResponseEntity.ok(myBooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookInfo> getBookInfo(@PathVariable String id) {
        BookInfo bookInfo = bookService.getBookInfo(id);
        bookInfo.setGpbkId(null);
        bookInfo.setJsonLink(null);
        bookInfo.setCategories(null);
        String thumbnail = bookInfo.getImageLinks().get("thumbnail");
        bookInfo.setImageLinks(Map.of("thumbnail", thumbnail));

        return ResponseEntity.ok(bookInfo);
    }

    @PostMapping("/{bookId}/update-read-history")
    public ResponseEntity<?> updateReadHistory(@PathVariable String bookId, @RequestBody UpdateReadHistoryRequest updateReadHistoryRequest) {
        readHistoryService.updateReadHistory(bookId, AuthUtils.getUsername(), updateReadHistoryRequest);
        return ResponseEntity.ok(ResponseStatus.SUCCESS.name());
    }

    @GetMapping("/last-read-books")
    public ResponseEntity<?> getLastReadBooks() {
        List<ReadHistory> readHistories = readHistoryService.getLast30ReadHistories(AuthUtils.getUsername());
//        System.out.println("read-histories: " + readHistories);

        List<LastReadDto> myLastReadBooks = bookService.getLastReadBooks(readHistories);
        return ResponseEntity.ok(myLastReadBooks);
    }

    @GetMapping("/recently-uploaded-books")
    public ResponseEntity<?> getRecentlyUploadedBooks() {
        List<BookInfoDto> bookInfoList = bookService.getRecentlyUploadedBooks();
        for (var i : bookInfoList) {
            i.setGenre(null);
            i.setAuthors(null);
        }
        return ResponseEntity.ok(bookInfoList);
    }

    @GetMapping("/{bookId}/chapters")
    public ResponseEntity<?> getChapterList(@PathVariable String bookId) {
        try {
            return ResponseEntity.ok(bookService.getBookChapters(bookId));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping("/update-quiz-test-history")
    public ResponseEntity<?> updateQuizTestHistory(@RequestBody TestUpdateRequest request) {
        testService.updateTestHistory(request, TestType.QUIZ);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam(required = true) String q,
                                         @RequestParam(defaultValue = "10") int limit) {
        List<BookInfoDto> bookInfoDtos = bookService.searchBooks(q, limit);
        return ResponseEntity.ok(bookInfoDtos);
    }


//    private void startFileProcessing(String uploadId, MultipartFile file) throws IOException {
//        Sinks.Many<JobStatus> sink = Sinks.many().multicast().onBackpressureBuffer();
//        statuses.put(uploadId, sink);

//        sink.tryEmitNext(new JobStatus("Preparing file for processing..."));
        // Upload file to temporary storage (e.g., GCloud)
//        String tempLocation = gCloudService.uploadFile(uploadId, file);

//        String callbackUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/book/server-status")
//                .path("/" + uploadId)
//                .toUriString();  // URL to accept status updates

//        sink.tryEmitNext(new JobStatus("Requesting additional processing..."));

//        CompletableFuture.runAsync(() -> {  // Asynchronously call Python backend
//                try {
//            Map<Object, Object> response = pythonBackendService.sendFileProcessingRequest(tempLocation, uploadId, callbackUrl);
//            ObjectMapper objectMapper = new ObjectMapper();
//            BookInfo book = objectMapper.convertValue(response, BookInfo.class);
//            System.out.println("Response: " + book.toString());

//            statuses.get(uploadId).tryEmitNext(new JobStatus("Processing complete", book));
//            statuses.get(uploadId).tryEmitComplete();
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                    statuses.put(uploadId, "Error occurred during analysis.");
//                }
//        });
//    }

//    @GetMapping("/status/{uploadId}")
//    public Flux<JobStatus> getStatus(@PathVariable String uploadId) {
//        Sinks.Many<JobStatus> sink = statuses.get(uploadId);
//        if (sink == null) {
//            return Flux.error(new RuntimeException("Job not found or already completed!"));
//        }
//        return sink.asFlux();
//    }

//    @PostMapping("/server-status/{uploadId}")
//    public ResponseEntity<String> receiveProcessingStatus(@PathVariable String uploadId, @RequestBody String status) {
//        System.out.println("Received status update for file " + uploadId + ": " + status);
//        return ResponseEntity.ok("Status received");
//    }
}

