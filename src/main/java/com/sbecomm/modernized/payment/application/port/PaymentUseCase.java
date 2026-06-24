package com.sbecomm.modernized.payment.application.port;

import com.sbecomm.modernized.payment.application.dto.request.ProcessPaymentRequest;
import com.sbecomm.modernized.payment.application.dto.response.PaymentResponse;

public interface PaymentUseCase {
    PaymentResponse processPayment(String userId, ProcessPaymentRequest request);
    PaymentResponse refundPayment(String userId, String paymentId);
    PaymentResponse getPaymentStatusByOrderId(String userId, String orderId);
}
