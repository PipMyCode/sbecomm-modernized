package com.sbecomm.modernized.payment.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProcessPaymentRequest(
    @NotBlank(message = "Order ID is required") String orderId,
    @NotBlank(message = "Payment method ID is required") String paymentMethodId // e.g., Stripe PaymentMethod ID or token
) {}
