package com.sbecomm.modernized.payment.infrastructure.adapter;

import com.sbecomm.modernized.payment.domain.repository.PaymentGatewayPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class StripePaymentGatewayAdapter implements PaymentGatewayPort {

    // In a production environment, we would inject the Stripe SDK Client here:
    // private final StripeClient stripeClient;

    @Override
    public PaymentGatewayResult charge(String paymentId, BigDecimal amount, String currency, String paymentMethodId) {
        try {
            // Simulated Stripe charge operation
            String mockTransactionId = "ch_" + UUID.randomUUID().toString().replace("-", "");
            return new PaymentGatewayResult(true, mockTransactionId, null);
        } catch (Exception e) {
            return new PaymentGatewayResult(false, null, "Stripe API Error: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayResult refund(String transactionId) {
        try {
            // Simulated Stripe refund operation
            return new PaymentGatewayResult(true, "re_" + UUID.randomUUID().toString().replace("-", ""), null);
        } catch (Exception e) {
            return new PaymentGatewayResult(false, null, "Stripe Refund Error: " + e.getMessage());
        }
    }
}
