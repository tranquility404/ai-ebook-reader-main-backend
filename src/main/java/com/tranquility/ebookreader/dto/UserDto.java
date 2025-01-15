package com.tranquility.ebookreader.dto;

import lombok.Data;

@Data
public class UserDto {
    private String profilePicCloudUrl;
    private String name;
    private String email;
    private String dob;
    private String country;
}

