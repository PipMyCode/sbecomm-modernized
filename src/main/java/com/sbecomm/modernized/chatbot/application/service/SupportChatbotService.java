package com.sbecomm.modernized.chatbot.application.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class SupportChatbotService {

    private final ChatClient chatClient;

    public SupportChatbotService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultSystem("You are a helpful E-Commerce customer support agent. Be polite and concise. You can fetch order status and answer questions about shipping and returns based on the provided store policy.")
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .defaultFunctions("orderStatus")
                .build();
    }

    public String chat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
