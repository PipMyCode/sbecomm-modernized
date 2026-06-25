package com.sbecomm.modernized.payment.domain.repository;

import java.math.BigDecimal;

/**
 * Outbound port strictly dedicated to integrating with an external Payment Gateway (e.g., Stripe)
 */
public interface PaymentGatewayPort {
    PaymentGatewayResult charge(String paymentId, BigDecimal amount, String currency, String paymentMethodId);

    PaymentGatewayResult refund(String transactionId);

    record PaymentGatewayResult(boolean success,
                                String transactionId,
                                String failureReason) {
    }
}
