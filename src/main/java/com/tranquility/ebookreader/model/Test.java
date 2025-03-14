package com.tranquility.ebookreader.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "test-history")
@Data
public class Test {
    @Id
    private String id;
    private String testId;
    private String studentId;
    private int score;
    private TestType testType;

    public Test(String testId, String studentId, int score, TestType testType) {
        this.testId = testId;
        this.studentId = studentId;
        this.score = score;
        this.testType = testType;
    }
}
