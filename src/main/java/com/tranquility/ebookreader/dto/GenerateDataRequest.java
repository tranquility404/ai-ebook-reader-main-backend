package com.tranquility.ebookreader.dto;

import lombok.Data;

@Data
public class GenerateDataRequest {
    private String bookId;
    private String chapterHref;
}
