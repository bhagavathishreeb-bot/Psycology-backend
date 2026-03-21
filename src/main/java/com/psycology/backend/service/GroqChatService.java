package com.psycology.backend.service;

import com.psycology.backend.config.GroqProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GroqChatService {

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final Logger log = LoggerFactory.getLogger(GroqChatService.class);

    private static final String SYSTEM_PROMPT = """
        You are a compassionate and knowledgeable psychology assistant for ManoTaranga, a mental wellness platform. \
        Your role is to provide supportive, evidence-based information about mental health, emotions, coping strategies, \
        and general psychological well-being. \
        \
        Guidelines: \
        - Be empathetic, non-judgmental, and supportive in your responses. \
        - Provide helpful information but always recommend seeking professional help (therapist, psychologist, or counselor) \
        for serious concerns, diagnoses, or crisis situations. \
        - You are not a substitute for professional mental health care. \
        - Use clear, accessible language. \
        - If asked about topics outside psychology, gently redirect to mental wellness when appropriate, \
        or briefly answer and offer to discuss psychology-related aspects. \
        - Do not provide medical diagnoses or prescribe treatments. \
        - For crisis situations (suicidal thoughts, self-harm, abuse), encourage immediate professional help \
        and provide crisis helpline information when relevant (e.g., India: 9152987821, 080-46110007).
        """;

    private final GroqProperties groqProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public GroqChatService(GroqProperties groqProperties) {
        this.groqProperties = groqProperties;
    }

    public String chat(String userMessage, List<Map<String, String>> conversationHistory) {
        if (groqProperties.getApiKey() == null || groqProperties.getApiKey().isBlank()) {
            log.warn("Groq API key not configured.");
            return "The chatbot is not configured. Please contact support.";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqProperties.getApiKey());

        List<Map<String, Object>> messages = new java.util.ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));

        if (conversationHistory != null && !conversationHistory.isEmpty()) {
            for (Map<String, String> msg : conversationHistory) {
                String role = msg.get("role");
                String content = msg.get("content");
                if (role != null && content != null) {
                    messages.add(Map.of("role", role, "content", content));
                }
            }
        }

        messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> body = Map.of(
            "model", groqProperties.getModel(),
            "messages", messages,
            "max_completion_tokens", 2048,
            "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.exchange(
                GROQ_API_URL, HttpMethod.POST, request, Map.class
            ).getBody();

            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null && message.containsKey("content")) {
                        return (String) message.get("content");
                    }
                }
            }
            log.error("Unexpected Groq API response: {}", response);
            return "I'm sorry, I couldn't generate a response. Please try again.";
        } catch (Exception e) {
            log.error("Groq API error: {}", e.getMessage());
            return "I'm sorry, I'm having trouble responding right now. Please try again later.";
        }
    }
}
