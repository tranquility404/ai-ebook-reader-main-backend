package com.tranquility.ebookreader.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class PythonBackendService {

    @Value("${python.backend.url}")
    private String pythonBackendUrl;

    private RestTemplate restTemplate;

    public PythonBackendService() {
        this.restTemplate = new RestTemplate();
    }

    public String healthCheck() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(pythonBackendUrl + "/health-check").toUriString();
            return restTemplate.getForObject(url, String.class);
        } catch (RuntimeException e) {
            return "Connection Refused";
        }
    }

    public String generateSummary(List<String> texts) {
        String url = UriComponentsBuilder.fromHttpUrl(pythonBackendUrl + "/generate-summary").toUriString();
        String response = restTemplate.postForObject(url, texts, String.class);
        return response;
    }

    public String generateQuiz(String summary) {
        String url = UriComponentsBuilder.fromHttpUrl(pythonBackendUrl + "/generate-quiz").toUriString();
        String response = restTemplate.postForObject(url, summary, String.class);
        return response;
    }

}
