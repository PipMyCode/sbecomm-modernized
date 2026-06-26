package com.sbecomm.modernized.chatbot.presentation.rest;

import com.sbecomm.modernized.chatbot.application.service.SupportChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String reply = chatbotService.chat(request.message());
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}
