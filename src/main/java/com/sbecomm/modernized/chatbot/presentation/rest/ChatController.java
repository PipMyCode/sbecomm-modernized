package com.sbecomm.modernized.chatbot.presentation.rest;

import com.sbecomm.modernized.chatbot.application.service.SupportChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final SupportChatbotService chatbotService;

    public ChatController(SupportChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    public record ChatRequest(String message) {}
    public record ChatResponse(String reply) {}

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponse> chat(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ChatRequest request) {
        // Enforce BOLA by explicitly passing the authenticated user's ID
        String reply = chatbotService.chat(jwt.getSubject(), request.message());
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}
