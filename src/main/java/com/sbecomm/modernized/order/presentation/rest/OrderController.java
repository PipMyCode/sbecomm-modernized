package com.sbecomm.modernized.order.presentation.rest;

import com.sbecomm.modernized.order.application.dto.request.CreateOrderRequest;
import com.sbecomm.modernized.order.application.dto.response.OrderResponse;
import com.sbecomm.modernized.order.application.port.OrderUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderUseCase orderUseCase;



    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("Received request to create order from cart for user: {}", jwt.getSubject());
        // Enforce BOLA: we pass the JWT subject as the verified user identity
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderUseCase.createOrderFromCart(jwt.getSubject(), request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        log.info("Fetching orders for user: {}", jwt.getSubject());
        return ResponseEntity.ok(orderUseCase.getUserOrders(jwt.getSubject()));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> getOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String orderId) {
        log.info("Fetching order details for user: {}, orderId: {}", jwt.getSubject(), orderId);
        return ResponseEntity.ok(orderUseCase.getOrder(jwt.getSubject(), orderId));
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String orderId) {
        log.info("Processing request to cancel order for user: {}, orderId: {}", jwt.getSubject(), orderId);
        return ResponseEntity.ok(orderUseCase.cancelOrder(jwt.getSubject(), orderId));
    }
}
