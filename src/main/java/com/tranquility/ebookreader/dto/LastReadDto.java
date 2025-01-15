package com.tranquility.ebookreader.dto;

import lombok.Data;

@Data
public class LastReadDto {
    private String id;
    private String title;
    private String thumbnail;
    private int progress;
}
