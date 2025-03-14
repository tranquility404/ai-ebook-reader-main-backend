package com.tranquility.ebookreader.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String profilePicCloudUrl;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dob;
    private String country;
    private Set<String> roles;
    private boolean banned;
    private int tier = 0;

    // Constructors, getters, and setters
}

