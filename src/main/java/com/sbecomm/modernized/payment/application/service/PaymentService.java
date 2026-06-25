package com.sbecomm.modernized.payment.application.service;

import com.sbecomm.modernized.order.application.dto.response.OrderResponse;
import com.sbecomm.modernized.order.application.port.OrderUseCase;
import com.sbecomm.modernized.payment.application.dto.request.ProcessPaymentRequest;
import com.sbecomm.modernized.payment.application.dto.response.PaymentResponse;
import com.sbecomm.modernized.payment.application.port.PaymentUseCase;
import com.sbecomm.modernized.payment.domain.model.Payment;
import com.sbecomm.modernized.payment.domain.repository.PaymentGatewayPort;
import com.sbecomm.modernized.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayPort paymentGatewayPort;
    private final OrderUseCase orderUseCase; // Cross-context dependency

    @Override
    @Transactional
    public PaymentResponse processPayment(String userId, ProcessPaymentRequest request) {
        // Enforce BOLA & retrieve exact cost directly from the authoritative source (Order)
        OrderResponse order = orderUseCase.getOrder(userId, request.orderId());

        if (paymentRepository.findByOrderId(order.id()).isPresent()) {
            throw new IllegalStateException("Payment already exists or is processing for this order");
        }

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                order.id(),
                order.totalAmount(),
                "USD",
                LocalDateTime.now()
        );
        payment.markProcessing();
        paymentRepository.save(payment);

        // Interface with Stripe
        PaymentGatewayPort.PaymentGatewayResult result = paymentGatewayPort.charge(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                request.paymentMethodId()
        );

        if (result.success()) {
            payment.markSucceeded(result.transactionId());
            // Future iteration: Emit an event to the Order Bounded Context to change status to PAID
        } else {
            payment.markFailed(result.failureReason());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return toResponse(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(String userId, String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        // Enforce BOLA
        orderUseCase.getOrder(userId, payment.getOrderId());

        PaymentGatewayPort.PaymentGatewayResult result = paymentGatewayPort.refund(payment.getTransactionId());

        if (result.success()) {
            payment.markRefunded();
            paymentRepository.save(payment);
        } else {
            throw new IllegalStateException("Refund failed: " + result.failureReason());
        }

        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentStatusByOrderId(String userId, String orderId) {
        // Enforce BOLA
        orderUseCase.getOrder(userId, orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No payment found for order: " + orderId));

        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(), payment.getOrderId(), payment.getAmount(),
                payment.getCurrency(), payment.getStatus().name(),
                payment.getTransactionId(), payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }
}
