package com.tranquility.ebookreader.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String email;
    private String password;
}

