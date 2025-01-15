package com.tranquility.ebookreader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "summaries")
@Data
public class Summary {
    @Id
    private String id;
    private String resourceId;
    private String bookId;
    private String chapterHref;
    private String requestedBy;

    public Summary(String resourceId, String bookId, String chapterHref, String requestedBy) {
        this.resourceId = resourceId;
        this.bookId = bookId;
        this.chapterHref = chapterHref;
        this.requestedBy = requestedBy;
    }
}
