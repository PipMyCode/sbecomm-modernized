package com.sbecomm.modernized.payment.domain.repository;

import com.sbecomm.modernized.payment.domain.model.Payment;
import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> findById(String id);
    Optional<Payment> findByOrderId(String orderId);
    Payment save(Payment payment);
}
