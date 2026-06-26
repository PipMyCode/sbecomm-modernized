package com.sbecomm.modernized.chatbot.application.service;

import com.sbecomm.modernized.order.application.dto.response.OrderResponse;
import com.sbecomm.modernized.order.application.port.OrderUseCase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class SupportChatbotService {

    private final ChatClient chatClient;
    private final OrderUseCase orderUseCase;

    public SupportChatbotService(ChatClient.Builder builder, VectorStore vectorStore, OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
        this.chatClient = builder
                .defaultSystem("You are a helpful E-Commerce customer support agent. Be polite and concise. You can fetch order status and answer questions about shipping and returns based on the provided store policy.")
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .build();
    }

    public record OrderStatusRequest(String orderId) {}

    public String chat(String userId, String message) {
        // Enforce BOLA by securely capturing the explicit userId in the closure, completely avoiding ThreadLocals
        Function<OrderStatusRequest, OrderResponse> orderStatusFunction = 
            request -> orderUseCase.getOrder(userId, request.orderId());

        FunctionCallbackWrapper<OrderStatusRequest, OrderResponse> callback = 
            FunctionCallbackWrapper.builder(orderStatusFunction)
                .withName("orderStatus")
                .withDescription("Get the status and details of a customer's order by their order ID")
                .build();

        return chatClient.prompt()
                .user(message)
                .options(OpenAiChatOptions.builder().withFunctionCallbacks(List.of(callback)).build())
                .call()
                .content();
    }
}
