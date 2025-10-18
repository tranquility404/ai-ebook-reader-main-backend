package com.tranquility.ebookreader.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String modelName;
    private final int maxTokens;
    private final String baseUrl = "https://api.groq.com/openai/v1/chat/completions";

    public AIService(
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.model.name:llama-3.3-70b-versatile}") String modelName,
            @Value("${groq.max.tokens:3072}") int maxTokens
    ) {
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.maxTokens = maxTokens;
        this.restTemplate = new RestTemplate();
    }

    public String generateSummary(List<String> texts) {
        StringBuilder combinedSummary = new StringBuilder();
        
        for (String text : texts) {
            if (text != null && !text.trim().isEmpty()) {
                int summaryLen = Math.max(1, Math.round(text.split("\\s+").length * 0.3f));
                String summary = sendRequest("you are an excellent summarizer. Summarize this text in " + summaryLen + " words (NO PREAMBLE):", text);
                
                if (combinedSummary.length() > 0) {
                    combinedSummary.append(" ");
                }
                combinedSummary.append(summary);
            }
        }
        
        return combinedSummary.toString();
    }

    public String generateQuiz(String summary) {
        int noOfQues = 25;
        return sendRequest("you are an excellent quizmaster. Generate " + noOfQues + " factual questions in JSON format (NO PREAMBLE):" +
                         """
                         #####################################
                         [ {{ "question": "...",
                             "options": {{
                                 "1": "...option",
                                 "2": "...option",
                                 "3": "...option",
                                 "4": "...option"
                             }},
                             "correctId": "correct-option-id"
                         }},...]
                         #####################################
                         Text:
                        """,
                summary);
    }

    private String sendRequest(String systemMsg, String userMsg) {
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemMsg);

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userMsg);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", modelName);
        payload.put("messages", List.of(systemMessage, userMessage));
        payload.put("max_tokens", maxTokens);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map choice = ((List<Map>) response.getBody().get("choices")).get(0);
            Map message = (Map) choice.get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating summary: " + e.getMessage();
        }
    }
}