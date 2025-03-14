package com.tranquility.ebookreader.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String dob;
    private String country;
}
