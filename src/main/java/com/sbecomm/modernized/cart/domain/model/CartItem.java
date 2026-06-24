package com.sbecomm.modernized.cart.domain.model;

import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

import com.sbecomm.modernized.catalog.domain.model.ProductId;

@Getter
public class CartItem {
    private final ProductId productId;
    private int quantity;
    private final BigDecimal unitPrice;

    public CartItem(@NonNull ProductId productId, int quantity, @NonNull BigDecimal unitPrice) {
        if (productId.value() == null || productId.value().isBlank())
            throw new IllegalArgumentException("Product ID cannot be blank");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Unit price cannot be negative");

        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = newQuantity;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
