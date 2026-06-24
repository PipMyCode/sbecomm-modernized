package com.sbecomm.modernized.order.infrastructure.entity;

import com.sbecomm.modernized.order.domain.model.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Column(name = "applied_promotion_code")
    private String appliedPromotionCode;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private java.math.BigDecimal discountPercentage;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public List<OrderItemEntity> getItems() { return items; }
    public void setItems(List<OrderItemEntity> items) {
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
