package com.sbecomm.modernized.payment.infrastructure.adapter;

import com.sbecomm.modernized.payment.domain.model.Payment;
import com.sbecomm.modernized.payment.domain.repository.PaymentRepository;
import com.sbecomm.modernized.payment.infrastructure.entity.PaymentEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Payment> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return jpaRepository.findByOrderId(orderId).map(this::toDomain);
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = toEntity(payment);
        return toDomain(jpaRepository.save(entity));
    }

    private Payment toDomain(PaymentEntity entity) {
        Payment payment = new Payment(
                entity.getId(), entity.getOrderId(), entity.getAmount(),
                entity.getCurrency(), entity.getCreatedAt()
        );
        payment.reconstructState(entity.getStatus(), entity.getTransactionId(), entity.getFailureReason());
        return payment;
    }

    private PaymentEntity toEntity(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(payment.getId());
        entity.setOrderId(payment.getOrderId());
        entity.setAmount(payment.getAmount());
        entity.setCurrency(payment.getCurrency());
        entity.setStatus(payment.getStatus());
        entity.setTransactionId(payment.getTransactionId());
        entity.setFailureReason(payment.getFailureReason());
        entity.setCreatedAt(payment.getCreatedAt());
        return entity;
    }
}
