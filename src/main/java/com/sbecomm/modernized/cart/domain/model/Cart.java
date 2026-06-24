package com.sbecomm.modernized.cart.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import com.sbecomm.modernized.user.domain.model.UserId;
import com.sbecomm.modernized.catalog.domain.model.ProductId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Cart {
    @NonNull
    private final UserId userId; // Acts as the aggregate root ID
    private final List<CartItem> items = new ArrayList<>();

    private String appliedPromotionCode;
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    public void addItem(CartItem item) {
        this.items.stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.updateQuantity(existing.getQuantity() + item.getQuantity()),
                        () -> this.items.add(item)
                );
    }

    public void addItem(ProductId productId, int quantity, BigDecimal unitPrice) {
        addItem(new CartItem(productId, quantity, unitPrice));
    }

    public void updateItemQuantity(ProductId productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(productId);
            return;
        }
        this.items.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresent(i -> i.updateQuantity(newQuantity));
    }

    public void removeItem(ProductId productId) {
        this.items.removeIf(i -> i.getProductId().equals(productId));
    }

    public void clear() {
        this.items.clear();
    }

    public void applyPromotion(String code, BigDecimal percentage) {
        this.appliedPromotionCode = code;
        this.discountPercentage = percentage != null ? percentage : BigDecimal.ZERO;
    }

    public void removePromotion() {
        this.appliedPromotionCode = null;
        this.discountPercentage = BigDecimal.ZERO;
    }

    public BigDecimal getSubTotal() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDiscountAmount() {
        if (discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getSubTotal()
                .multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalAmount() {
        return getSubTotal().subtract(getDiscountAmount());
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
