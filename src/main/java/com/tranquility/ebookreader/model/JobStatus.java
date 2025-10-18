package com.tranquility.ebookreader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class JobStatus {
    @NonNull
    private String status;
    private Object message;
}
