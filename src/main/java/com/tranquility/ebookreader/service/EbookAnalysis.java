package com.tranquility.ebookreader.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquility.ebookreader.model.BookInfo;
import com.tranquility.ebookreader.model.Chapter;
import lombok.Getter;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookAnalysis {

    @Getter
    private Book book;
    @Getter
    private BookInfo bookInfo;
    private RestTemplate restTemplate;

    @Value("${gplaybook.apikey}")
    private String apiKey;

    public EbookAnalysis() {
        this.restTemplate = new RestTemplate();
    }

    public void init(InputStream inputStream) throws IOException {
        this.book = new EpubReader().readEpub(inputStream);
        this.bookInfo = new BookInfo();
    }

    public void loadBookInfo() throws JsonProcessingException {
        bookInfo = fetchBookInfo();
        if (bookInfo == null) {
            System.out.println("Failed to get book info");
        } else {
            bookInfo.setLanguage(book.getMetadata().getLanguage());
        }
    }

    public void loadChaptersLite() throws IOException {
        bookInfo.setChapters(getChapters(book.getTableOfContents().getTocReferences(), new ArrayList<>()));
        bookInfo.setPageCount(bookInfo.getChapters().size());
    }

    private JsonNode findBestMatch(JsonNode jsonArray, String query) throws JsonProcessingException {
        JsonNode bestMatch = null;
        int bestScore = 0;

        for (var item : jsonArray) {
            String title = item.get("volumeInfo").get("title").toString();
            int score = FuzzySearch.weightedRatio(query.toLowerCase(), title.toLowerCase());  // Case-insensitive matching

            if (score > bestScore) {
                bestScore = score;
                bestMatch = item;
            }
        }

        return bestMatch;
    }

    private BookInfo fetchBookInfo() throws JsonProcessingException {
        String query = this.book.getTitle();
        System.out.println("book-title: " + query);
        String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/books/v1/volumes?q={query}&key={apiKey}")
                .buildAndExpand(query, apiKey)
                .toUriString();

        String response1 = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseBody = objectMapper.readTree(response1);

        if (responseBody != null && responseBody.has("items")) {
            JsonNode items = responseBody.get("items");
            JsonNode bestMatch = findBestMatch(items, query);

            String selfLink = bestMatch.get("selfLink").asText();
            String url2 = UriComponentsBuilder.fromHttpUrl(selfLink + "?key={apikey}")
                    .buildAndExpand(apiKey)
                    .toUriString();

            String response2 = restTemplate.getForObject(url2, String.class);
            JsonNode info = objectMapper.readTree(response2);
            return new BookInfo(info);
        }
        return null;
    }

    private String getChapterContentText(String content) {
        Document page = Jsoup.parse(content);
        String text = page.text();
        text = text.replaceAll("([.,!?;])(?=\\S)", "$1 ");
        text = text.replaceAll("\\s+", " ").trim();
        return text;
    }

    public List<String> breakChapterText(String[] words) {
        int threshold = 2000;
        List<String> textArr = new ArrayList<>();
        StringBuilder chunkBuilder = new StringBuilder();
        int wordCount = 0;

        for (String word : words) {
            if (!chunkBuilder.isEmpty()) {
                chunkBuilder.append(" ");
            }
            chunkBuilder.append(word);
            wordCount++;

            if (wordCount >= threshold) {
                int lastPeriodIndex = chunkBuilder.lastIndexOf(".");
                if (lastPeriodIndex != -1) {
                    textArr.add(chunkBuilder.substring(0, lastPeriodIndex + 1).trim());
                    chunkBuilder = new StringBuilder(chunkBuilder.substring(lastPeriodIndex + 1).trim());
                } else {
                    textArr.add(chunkBuilder.toString().trim());
                    chunkBuilder.setLength(0);
                }
                wordCount = chunkBuilder.toString().split(" ").length; // Recalculate remaining word count
            }
        }

        if (!chunkBuilder.isEmpty()) {
            textArr.add(chunkBuilder.toString().trim());
        }

        return textArr;
    }

    private Chapter getChapterLite(String href) throws IOException {
        Resource item = this.book.getResources().getByHref(href);
        if (item == null) {
            return null;
        }
        String content = new String(item.getData(), item.getInputEncoding());
        String[] words = getChapterContentText(content).split(" ");
        List<String> texts = new ArrayList<>();
//        List<String> texts = breakChapterText(words);
        int noOfWords = words.length;
        this.bookInfo.setTotalWords(this.bookInfo.getTotalWords() + noOfWords);

        return new Chapter(item.getId(), "", noOfWords, href, texts);
    }

    public Chapter getChapter(String href) throws IOException {
        Resource item = this.book.getResources().getByHref(href);
        if (item == null) {
            return null;
        }
        String content = new String(item.getData(), item.getInputEncoding());
        String[] words = getChapterContentText(content).split(" ");
        List<String> texts = breakChapterText(words);
        int noOfWords = words.length;
        this.bookInfo.setTotalWords(this.bookInfo.getTotalWords() + noOfWords);
        return new Chapter(item.getId(), "", noOfWords, href, texts);
    }

    private List<Chapter> getChapters(List<TOCReference> arr, List<Chapter> chapters) throws IOException {
        for (TOCReference item : arr) {
            if (!item.getChildren().isEmpty()) {
                getChapters(item.getChildren(), chapters);
            } else {
                Chapter chapter = getChapterLite(item.getResource().getHref());
                if (chapter != null) {
                    chapter.setTitle(item.getTitle());
                    chapters.add(chapter);
//                    System.out.println("chapter: " + item.getTitle());
                } else {
                    System.out.println(item.getTitle() + " not found");
                }
            }
        }
        return chapters;
    }

    public static void main(String[] args) {
        try {
            InputStream inputStream = new FileInputStream("./books/book2.epub");
            EbookAnalysis ebookAnalysis = new EbookAnalysis();
            ebookAnalysis.init(inputStream);
            List<Chapter> chapters = ebookAnalysis.getChapters(ebookAnalysis.getBook().getTableOfContents().getTocReferences(), new ArrayList<>());
            for (var i : chapters)
                System.out.println(i);
//            System.out.println(ebookAnalysis.getBookInfo());
//
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
