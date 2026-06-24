package com.sbecomm.modernized.cart.infrastructure.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class CartEntity {
    
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItemEntity> items = new ArrayList<>();

    @Column(name = "applied_promotion_code")
    private String appliedPromotionCode;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private java.math.BigDecimal discountPercentage;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartItemEntity> getItems() { return items; }
    
    public void setItems(List<CartItemEntity> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
    }

    public String getAppliedPromotionCode() { return appliedPromotionCode; }
    public void setAppliedPromotionCode(String appliedPromotionCode) { this.appliedPromotionCode = appliedPromotionCode; }

    public java.math.BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(java.math.BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
}
