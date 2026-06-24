package com.sbecomm.modernized.order.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    String id, 
    String userId, 
    LocalDateTime createdAt, 
    String status, 
    List<OrderItemResponse> items, 
    BigDecimal subTotal,
    String appliedPromotionCode,
    BigDecimal discountPercentage,
    BigDecimal discountAmount,
    BigDecimal totalAmount
) {}
