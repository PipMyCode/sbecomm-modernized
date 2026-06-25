package com.sbecomm.modernized.payment.domain.model;

import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Payment {
    private final String id;
    private final String orderId;
    private final BigDecimal amount;
    private final String currency;
    private PaymentStatus status;
    private String transactionId;
    private String failureReason;
    private final LocalDateTime createdAt;

    public Payment(@NonNull String id, @NonNull String orderId, @NonNull BigDecimal amount, @NonNull String currency, LocalDateTime createdAt) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.PENDING;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public void markProcessing() {
        if (this.status != PaymentStatus.PENDING)
            throw new IllegalStateException("Only PENDING payments can transition to PROCESSING");
        this.status = PaymentStatus.PROCESSING;
    }

    public void markSucceeded(@NonNull String transactionId) {
        if (this.status != PaymentStatus.PROCESSING && this.status != PaymentStatus.PENDING)
            throw new IllegalStateException("Invalid state transition to SUCCEEDED");
        this.status = PaymentStatus.SUCCEEDED;
        this.transactionId = transactionId;
    }

    public void markFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }

    public void markRefunded() {
        if (this.status != PaymentStatus.SUCCEEDED)
            throw new IllegalStateException("Only SUCCEEDED payments can be REFUNDED");
        this.status = PaymentStatus.REFUNDED;
    }

    public void reconstructState(PaymentStatus status, String transactionId, String failureReason) {
        this.status = status;
        this.transactionId = transactionId;
        this.failureReason = failureReason;
    }
}
