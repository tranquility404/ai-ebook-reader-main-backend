package com.tranquility.ebookreader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Chapter implements Serializable {
    private String uid;
    private String title;
    private int noOfWords;
    private String href;
    private List<String> texts;
}