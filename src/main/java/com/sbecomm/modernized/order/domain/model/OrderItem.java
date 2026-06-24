package com.sbecomm.modernized.order.domain.model;

import lombok.Getter;
import lombok.NonNull;
import java.math.BigDecimal;

@Getter
public class OrderItem {
    private final String productId;
    private final int quantity;
    private final BigDecimal unitPrice; // Snapshot of the price at the time of order

    public OrderItem(@NonNull String productId, int quantity, @NonNull BigDecimal unitPrice) {
        if (productId.isBlank()) throw new IllegalArgumentException("Product ID cannot be blank");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Unit price cannot be negative");

        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
