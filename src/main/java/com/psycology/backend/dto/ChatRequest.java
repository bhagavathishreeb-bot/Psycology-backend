package com.psycology.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public class ChatRequest {

    @NotBlank(message = "Message is required")
    private String message;

    /**
     * Optional conversation history for context.
     * Each item: { "role": "user"|"assistant", "content": "..." }
     */
    private List<Map<String, String>> history;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<Map<String, String>> getHistory() { return history; }
    public void setHistory(List<Map<String, String>> history) { this.history = history; }
}
