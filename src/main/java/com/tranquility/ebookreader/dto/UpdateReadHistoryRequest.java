package com.tranquility.ebookreader.dto;

import lombok.Data;

@Data
public class UpdateReadHistoryRequest {
    private int pageIdx;
    private String time;
}
