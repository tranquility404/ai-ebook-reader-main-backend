package com.tranquility.ebookreader.controller;

import com.tranquility.ebookreader.dto.GenerateDataRequest;
import com.tranquility.ebookreader.model.*;
import com.tranquility.ebookreader.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/main-ml")
public class MLTaskController {

    private final SummaryService summaryService;
    private final QuizService quizService;
    private final BookService bookService;
    private final GCloudService gCloudService;
    private final EbookAnalysis ebookAnalysis;
    private final MLService mlService;

    public MLTaskController(SummaryService summaryService, QuizService quizService, BookService bookService,
                            GCloudService gCloudService, EbookAnalysis ebookAnalysis,
                            MLService mlService) {
        this.summaryService = summaryService;
        this.quizService = quizService;
        this.bookService = bookService;
        this.gCloudService = gCloudService;
        this.ebookAnalysis = ebookAnalysis;
        this.mlService = mlService;
    }

//    @GetMapping("/health-check")
//    public ResponseEntity<?> healthCheck() {
//        String healthRes = mlService.healthCheck();
//        if (healthRes.equals("HEALTH-OK"))
//            return ResponseEntity.ok(healthRes);
//        else return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorMessage("UNAVAILABLE"));
//    }

    @PostMapping("/generate-summary")
    public ResponseEntity<?> generateSummary(@RequestBody GenerateDataRequest request) {
        Summary summary = summaryService.getSummary(request.getBookId(), request.getChapterHref());
        if (summary != null) {   // Already generated
            System.out.println("Found cached summary");
            return ResponseEntity.ok(gCloudService.getFileContent(summary.getResourceId()));
        }

//        Not generated yet, generating now...
        try {
            BookInfo info = getAIRequestData(request);
            System.out.println(info.getCloudUrl());
            try (InputStream is = gCloudService.downloadFile(info.getCloudUrl())) {
                ebookAnalysis.init(is);
                Chapter chapter = ebookAnalysis.getChapter(request.getChapterHref());
                if (chapter == null) throw new RuntimeException("Chapter not found");

                String generatedSummary = mlService.generateSummary(chapter.getTexts()); // Generate Summary
                String resourceId = gCloudService.uploadSummaryTxtToCloud(generatedSummary); // Save summary to cloud
                summaryService.addSummary(resourceId, request); // Save Summary info in database
                return ResponseEntity.ok(generatedSummary);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Something went wrong!");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @PostMapping("/generate-quiz")
    public ResponseEntity<?> generateQuiz(@RequestBody GenerateDataRequest request) {
        Quiz quiz = quizService.getQuiz(request.getBookId(), request.getChapterHref());
        if (quiz != null) {   // Already generated
            System.out.println("Found cached quiz");
            return ResponseEntity.ok(Map.of("quizId", quiz.getId()));
        }

//        Not generated yet, generating now...
        try {
            Summary summary = summaryService.getSummary(request.getBookId(), request.getChapterHref());
            if (summary == null) throw new RuntimeException("SUMMARY NOT FOUND");

            System.out.println("Summary found for quiz generation");
            String summaryContent = gCloudService.getFileContent(summary.getResourceId());
            if (summaryContent.split(" ").length > 2000) throw new RuntimeException("Token limit exceeded");

            String generatedQuiz = mlService.generateQuiz(summaryContent); // Generate Quiz
            String resourceId = gCloudService.uploadQuizJsonToCloud(generatedQuiz); // Save quiz to cloud
            String id = quizService.addQuiz(resourceId, request); // Save quiz info in database
            return ResponseEntity.ok(Map.of("quizId", id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<?> getQuiz(@PathVariable String quizId) {
        Quiz quiz = quizService.getQuiz(quizId);
        if (quiz == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("Quiz Not found!"));
        return ResponseEntity.ok(gCloudService.getFileContent(quiz.getResourceId()));
    }

    public BookInfo getAIRequestData(GenerateDataRequest request) {
        BookInfo bookInfo = bookService.getBookInfoForAI(request.getBookId());
        if (bookInfo == null) throw new RuntimeException("Book not found");

        return bookInfo;
//        System.out.println(bookInfo.getChapters());
//        Chapter chapter = null;
//        for (var ch: bookInfo.getChapters()) {
//            if (request.getChapterHref().equals(ch.getHref())) {
//                chapter = ch;
//                break;
//            }
//        }
//        if (chapter == null) throw new RuntimeException("Chapter not found");
//
//        return new AbstractMap.SimpleEntry<>(bookInfo.getCloudUrl(), chapter);
    }
}
