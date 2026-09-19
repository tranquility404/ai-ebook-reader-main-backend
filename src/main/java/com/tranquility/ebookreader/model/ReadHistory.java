package com.tranquility.ebookreader.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "read-history")
@Data
public class ReadHistory {
    @Id
    private String id;
    private String bookId;
    private String userId;
    private int pageIdx;
    private String time;

    public ReadHistory(String bookId, String userId, int pageIdx, String time) {
        this.bookId = bookId;
        this.userId = userId;
        this.pageIdx = pageIdx;
        this.time = time;
    }
}
