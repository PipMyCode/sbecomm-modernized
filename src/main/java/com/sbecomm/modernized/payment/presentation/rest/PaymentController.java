package com.sbecomm.modernized.payment.presentation.rest;

import com.sbecomm.modernized.payment.application.dto.request.ProcessPaymentRequest;
import com.sbecomm.modernized.payment.application.dto.response.PaymentResponse;
import com.sbecomm.modernized.payment.application.port.PaymentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentUseCase paymentUseCase;



    @PostMapping("/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> processPayment(
            @AuthenticationPrincipal Jwt jwt, 
            @Valid @RequestBody ProcessPaymentRequest request) {
        // BOLA is handled by injecting the user subject UUID directly
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentUseCase.processPayment(jwt.getSubject(), request));
    }

    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> refundPayment(
            @AuthenticationPrincipal Jwt jwt, 
            @PathVariable String paymentId) {
        return ResponseEntity.ok(paymentUseCase.refundPayment(jwt.getSubject(), paymentId));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> getPaymentStatusByOrderId(
            @AuthenticationPrincipal Jwt jwt, 
            @PathVariable String orderId) {
        return ResponseEntity.ok(paymentUseCase.getPaymentStatusByOrderId(jwt.getSubject(), orderId));
    }
}
