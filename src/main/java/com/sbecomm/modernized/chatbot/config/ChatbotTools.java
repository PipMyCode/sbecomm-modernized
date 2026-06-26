package com.sbecomm.modernized.chatbot.config;

import com.sbecomm.modernized.order.application.dto.response.OrderResponse;
import com.sbecomm.modernized.order.application.port.OrderUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Function;

@Configuration
public class ChatbotTools {

    public record OrderStatusRequest(String orderId) {}

    @Bean
    @Description("Get the status and details of a customer's order by their order ID")
    public Function<OrderStatusRequest, OrderResponse> orderStatus(OrderUseCase orderUseCase) {
        return request -> {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            return orderUseCase.getOrder(userId, request.orderId());
        };
    }
}
