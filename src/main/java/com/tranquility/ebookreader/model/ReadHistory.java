package com.tranquility.ebookreader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "read-history")
@Data
@AllArgsConstructor
public class ReadHistory {
    @Id
    private String bookId;
    private String userId;
    private int pageIdx;
    private String time;
}
