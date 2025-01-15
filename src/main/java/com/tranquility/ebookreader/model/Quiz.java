package com.tranquility.ebookreader.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quiz")
@Data
public class Quiz {
    @Id
    private String id;
    private String resourceId;
    private String bookId;
    private String chapterHref;
    private String requestedBy;

    public Quiz(String resourceId, String bookId, String chapterHref, String requestedBy) {
        this.resourceId = resourceId;
        this.bookId = bookId;
        this.chapterHref = chapterHref;
        this.requestedBy = requestedBy;
    }
}
