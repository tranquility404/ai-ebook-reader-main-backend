package com.tranquility.ebookreader.dto;

import com.tranquility.ebookreader.model.Chapter;
import lombok.Data;

import java.util.List;

@Data
public class BookInfoDto {
    private String id = null;
    private String thumbnail;
    private String title;
    private List<Chapter> chapters;
    private List<String> authors;
    private int pageCount;
    private String genre;
    private String maturityRating;
    private String language;
    private String postedBy;
}