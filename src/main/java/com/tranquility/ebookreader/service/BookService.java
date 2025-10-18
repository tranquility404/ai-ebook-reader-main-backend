package com.tranquility.ebookreader.service;

import com.tranquility.ebookreader.dto.BookInfoDto;
import com.tranquility.ebookreader.dto.LastReadDto;
import com.tranquility.ebookreader.model.BookInfo;
import com.tranquility.ebookreader.model.Chapter;
import com.tranquility.ebookreader.model.ReadHistory;
import com.tranquility.ebookreader.repository.BookRepository;
import com.tranquility.ebookreader.utils.AuthUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final int AVG_LIMIT = 10;

    private MongoTemplate mongoTemplate;
    private final BookRepository bookRepository;

    public BookService(MongoTemplate mongoTemplate, BookRepository bookRepository) {
        this.mongoTemplate = mongoTemplate;
        this.bookRepository = bookRepository;
    }

    public void addBook(BookInfo bookInfo) {
        BookInfo savedBook = bookRepository.save(bookInfo);
    }

    public List<BookInfoDto> getMyCollection(String userId) {
        return convertToBookDtos(bookRepository.findByPostedBy(userId, PageRequest.of(0, AVG_LIMIT)));
    }

    public List<LastReadDto> getLastReadBooks(List<ReadHistory> readHistories) {
        List<String> bookIds = readHistories.stream().map(ReadHistory::getBookId)
                .collect(Collectors.toList());
        List<BookInfo> bookInfoList = bookRepository.findByIdIn(bookIds, PageRequest.of(0, AVG_LIMIT));
        return convertToLastReadDtos(readHistories, bookInfoList);
    }

    private List<LastReadDto> convertToLastReadDtos(List<ReadHistory> readHistories, List<BookInfo> bookInfoList) {
        List<LastReadDto> lastReadDtos = new ArrayList<>();
        Map<String, ReadHistory> map = new HashMap<>();
        for (var i : readHistories)
            map.put(i.getBookId(), i);

        for (var i : bookInfoList) {
            LastReadDto lastReadDto = new LastReadDto();
            lastReadDto.setId(i.getId());
            lastReadDto.setTitle(i.getTitle());
            lastReadDto.setThumbnail(i.getImageLinks().get("smallThumbnail"));

            int progress = Math.round((float) (map.get(i.getId()).getPageIdx() * 100) / i.getPageCount());
            System.out.println();
            lastReadDto.setProgress(progress);

            lastReadDtos.add(lastReadDto);
        }
        return lastReadDtos;
    }

    public List<BookInfoDto> convertToBookDtos(List<BookInfo> myBooks) {
        List<BookInfoDto> bookInfoDtos = new ArrayList<>();
        for (var i : myBooks) {
            BookInfoDto bookInfoDto = convertToDto(i);
            bookInfoDto.setChapters(null);
            bookInfoDto.setPageCount(0);
            bookInfoDto.setMaturityRating(null);
            bookInfoDto.setLanguage(null);
            bookInfoDtos.add(bookInfoDto);
        }
        return bookInfoDtos;
    }

    public BookInfo findByGpbkIdAndPostedBy(String gpbkId, String postedBy) {
        return bookRepository.findByGpbkIdAndPostedBy(gpbkId, postedBy);
    }

    public List<Chapter> getBookChapters(String bookId) {
        BookInfo info = bookRepository.findChaptersById(bookId);
        if (info != null) {
            return info.getChapters();
        } else
            throw new RuntimeException("Book not found!");
    }

    public BookInfo getBookInfoForAI(String bookId) {
        return bookRepository.findCloudUrlById(bookId);
    }

    public List<BookInfoDto> getRecentlyUploadedBooks() {
        List<BookInfo> bookInfoList = bookRepository.findByPostedByNotOrderByUploadedAtDesc(AuthUtils.getUsername(), PageRequest.of(0, AVG_LIMIT));
        return convertToBookDtos(bookInfoList);
    }

    public List<BookInfoDto> searchBooks(String query, int limit) {
        // Step 1: Convert query to lowercase & split into a word list (l1)
        List<String> l1 = List.of(query.toLowerCase().split("\\s+")); // converted query into wordlist

        // Step 2-7: Build the aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                // Step 2: Convert title property to lowercase
                Aggregation.project("title", "imageLinks", "authors", "genre", "postedBy")
                        .andExpression("toLower(title)").as("lowerTitle"),
                // Step 3: Match titles containing any word in l1
                Aggregation.match(Criteria.where("lowerTitle").regex(String.join("|", l1), "i")),
                // Step 4: Split the title into words (l2)
                Aggregation.project("title", "lowerTitle", "imageLinks", "authors", "genre", "postedBy")
                        .andExpression("split(lowerTitle, ' ')").as("l2"),
                // Step 5: Create list l3 by filtering l2 where values exist in l1
                Aggregation.project("title", "lowerTitle", "l2", "imageLinks", "authors", "genre", "postedBy")
                        .and(
                                ArrayOperators.Filter.filter("l2")
                                        .as("word")
                                        .by(Criteria.where("word").in(l1).toString())
                        ).as("l3"),
                // Step 6: Store the size of l3 as score
                Aggregation.project("title", "lowerTitle", "l3", "imageLinks", "authors", "genre", "postedBy")
                        .andExpression("size(l3)").as("score"),
                // Step 7: Sort records by score in descending order
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "score")),
                // Step 8: Get the first 10 records
                Aggregation.limit(limit)
        );

        AggregationResults<BookInfo> results = mongoTemplate.aggregate(aggregation, "books", BookInfo.class);

        List<BookInfo> books = results.getMappedResults();
        return convertToBookDtos(books);
    }

    public BookInfo getBookInfo(String id) {
        BookInfo book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return book;
    }

    public BookInfoDto convertToDto(BookInfo book) {
        BookInfoDto dto = new BookInfoDto();
        dto.setId(book.getId());
        if (book.getImageLinks() != null)
            dto.setThumbnail(book.getImageLinks().get("smallThumbnail"));
        dto.setTitle(book.getTitle());

        List<Chapter> chapters = book.getChapters();
        if (chapters != null)
            for (var i : chapters) {
                i.setUid(null);
                i.setHref(null);
                i.setHref(null);
            }
        dto.setChapters(chapters);

        dto.setAuthors(book.getAuthors());
        dto.setPageCount(book.getPageCount());
        dto.setGenre(book.getGenre());
        dto.setMaturityRating(book.getMaturityRating());
        dto.setLanguage(book.getLanguage());
        dto.setPostedBy(book.getPostedBy());
        return dto;
    }
}

