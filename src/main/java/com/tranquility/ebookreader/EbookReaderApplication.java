package com.tranquility.ebookreader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class EbookReaderApplication {

    public static void main(String[] args) {

        SpringApplication.run(EbookReaderApplication.class, args);
    }
}

