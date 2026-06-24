package com.sbecomm.modernized.order.domain.model;

import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Order {
    private final OrderId id;
    private final String userId; // Maps to Keycloak UUID
    private final LocalDateTime createdAt;
    private OrderStatus status;
    private final List<OrderItem> items;
    
    private String appliedPromotionCode;
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    public Order(@NonNull OrderId id, @NonNull String userId, LocalDateTime createdAt, OrderStatus status) {
        if (userId.isBlank()) throw new IllegalArgumentException("User ID cannot be blank");
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.status = status != null ? status : OrderStatus.CREATED;
        this.items = new ArrayList<>();
    }

    public void addOrderItem(@NonNull OrderItem item) {
        if (this.status != OrderStatus.CREATED) {
            throw new com.sbecomm.modernized.order.domain.exception.InvalidOrderStateException("Cannot modify items for an order not in CREATED state");
        }
        this.items.add(item);
    }

    public void markAsPaid() {
        if (this.status != OrderStatus.CREATED) {
            throw new com.sbecomm.modernized.order.domain.exception.InvalidOrderStateException("Only CREATED orders can be marked as PAID");
        }
        this.status = OrderStatus.PAID;
    }

    public void ship() {
        if (this.status != OrderStatus.PAID) {
            throw new com.sbecomm.modernized.order.domain.exception.InvalidOrderStateException("Only PAID orders can be SHIPPED");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new com.sbecomm.modernized.order.domain.exception.InvalidOrderStateException("Only SHIPPED orders can be DELIVERED");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new com.sbecomm.modernized.order.domain.exception.InvalidOrderStateException("Cannot cancel an order that has already been shipped or delivered");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void applyPromotion(String code, BigDecimal percentage) {
        if (this.status != OrderStatus.CREATED) {
            throw new com.sbecomm.modernized.order.domain.exception.InvalidOrderStateException("Cannot modify promotion for an order not in CREATED state");
        }
        this.appliedPromotionCode = code;
        this.discountPercentage = percentage != null ? percentage : BigDecimal.ZERO;
    }

    public BigDecimal getSubTotal() {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDiscountAmount() {
        if (discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getSubTotal().multiply(discountPercentage).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalAmount() {
        return getSubTotal().subtract(getDiscountAmount());
    }

    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
}
