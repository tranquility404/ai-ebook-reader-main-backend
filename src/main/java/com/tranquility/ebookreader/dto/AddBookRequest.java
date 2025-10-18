package com.tranquility.ebookreader.dto;

import com.tranquility.ebookreader.model.BookInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddBookRequest {
    private BookInfo bookInfo;
    private byte[] file;
}
