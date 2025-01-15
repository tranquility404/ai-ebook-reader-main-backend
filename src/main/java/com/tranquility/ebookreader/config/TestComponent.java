package com.tranquility.ebookreader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TestComponent {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @PostConstruct
    public void demo() {
        System.out.println(maxFileSize);
        System.out.println(maxRequestSize);
//        System.out.println(maxHttpPostSize);
    }
}
