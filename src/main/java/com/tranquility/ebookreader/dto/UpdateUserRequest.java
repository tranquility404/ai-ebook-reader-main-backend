package com.tranquility.ebookreader.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String dob;
    private String country;
}
