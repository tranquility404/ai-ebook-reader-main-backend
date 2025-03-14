package com.tranquility.ebookreader.service;

import com.tranquility.ebookreader.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class MLService {

    @Value("${ml.service.host}")
    private String mlServiceUri;

    @Value("${ml.service.port}")
    private String port;

    private RestTemplate restTemplate;

    public MLService() {
        this.restTemplate = new RestTemplate();
    }

//    public String healthCheck() {
//        try {
//            String url = UriComponentsBuilder.fromHttpUrl(mlServiceUri + "/health-check").toUriString();
//            return restTemplate.getForObject(url, String.class);
//        } catch (RuntimeException e) {
//            return "Connection Refused";
//        }
//    }

    public String generateSummary(List<String> texts) {
        String url = UriComponentsBuilder.newInstance().scheme("http").host(mlServiceUri).port(port).path("/users/summary").toUriString();
        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Roles", AuthUtils.getUserRoles());

        HttpEntity<List<String>> entity = new HttpEntity<>(texts, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    public String generateQuiz(String summary) {
        String url = UriComponentsBuilder.fromHttpUrl(mlServiceUri + "/users/quiz").toUriString();
        String response = restTemplate.postForObject(url, summary, String.class);
        return response;
    }

}
