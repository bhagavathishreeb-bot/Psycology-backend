package com.psycology.backend.controller;

import com.psycology.backend.dto.ChatRequest;
import com.psycology.backend.service.GroqChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final GroqChatService groqChatService;

    public ChatController(GroqChatService groqChatService) {
        this.groqChatService = groqChatService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@Valid @RequestBody ChatRequest request) {
        String response = groqChatService.chat(request.getMessage(), request.getHistory());
        return ResponseEntity.ok(Map.of("reply", response));
    }
}
