package com.sbecomm.modernized.payment.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
    String id,
    String orderId,
    BigDecimal amount,
    String currency,
    String status,
    String transactionId,
    String failureReason,
    LocalDateTime createdAt
) {}
