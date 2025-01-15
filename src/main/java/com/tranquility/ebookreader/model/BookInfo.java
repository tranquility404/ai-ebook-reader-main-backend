package com.tranquility.ebookreader.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.tranquility.ebookreader.utils.JsonUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.InputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Document(collection = "books")
@Data
@NoArgsConstructor
public class BookInfo implements Serializable {

    @Id
    private String id;

//    -------------------Extracted from Play Books & epub file-------------------------
    private String gpbkId;
    private String jsonLink;    //
    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private List<String> categories;
    private String genre;
    private String maturityRating;
    private Map<String, String> imageLinks;
    private String language;
    private List<Chapter> chapters;
    private int pageCount;
    private int totalWords;
//    ----------------------------------------------------------

//    private int views = 0;
//    likes, readBy
    private String postedBy;
    private String cloudUrl;

    @CreatedDate
    private Instant uploadedAt;

    // Constructor to initialize fields from a Map<String, Object>
    public BookInfo(JsonNode json) throws JsonProcessingException {
        this.gpbkId = json.get("id").asText();
        this.jsonLink = json.get("selfLink").asText();

       JsonNode volumeInfo = json.get("volumeInfo");
        this.title =  volumeInfo.get("title").asText();
        this.authors = JsonUtils.convertJsonToList(volumeInfo.get("authors").toString());
        this.publisher =  volumeInfo.get("publisher").asText();
        this.publishedDate =  volumeInfo.get("publishedDate").asText();
        this.description = parseHtml(volumeInfo.get("description").asText());
        this.categories = JsonUtils.convertJsonToList(volumeInfo.get("categories").toString());
        this.genre = genreFromCategory(this.categories.get(0)); // Assuming the first category contains the genre
        this.maturityRating =  volumeInfo.get("maturityRating").asText();
        this.imageLinks = JsonUtils.convertJsonToMap(volumeInfo.get("imageLinks").toString());

        // Default values
        this.language = "";
        this.chapters = null;
        this.totalWords = 0;
    }

    // Parse HTML tags (similar to Python's __parseHtml)
    private String parseHtml(String desc) {
        if (desc == null) return "";
        Pattern pattern = Pattern.compile("<[^>]+>");
        Matcher matcher = pattern.matcher(desc);
        return matcher.replaceAll("").trim();
    }

    // Extract genre from the first category (similar to Python's __genre)
    private String genreFromCategory(String firstCat) {
        int idx = firstCat.indexOf('/');
        if (idx != -1) {
            return firstCat.substring(0, idx).trim();
        }
        return firstCat.trim();
    }

    public boolean equals(BookInfo info) {
        return this.gpbkId.equals(info.getGpbkId()) &&
                this.title.equals(info.title) &&
                this.authors.equals(info.authors)  &&
                this.publisher.equals(info.getPublisher()) &&
                this.publishedDate.equals(info.getPublishedDate()) &&
                this.categories.equals(info.getCategories()) &&
                this.maturityRating.equals(info.getMaturityRating()) &&
                this.language.equals(info.getLanguage()) &&
                this.pageCount == info.getPageCount() &&
                this.postedBy.equals(info.getPostedBy());
    }
}

