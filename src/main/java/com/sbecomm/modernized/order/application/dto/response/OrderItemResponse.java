package com.sbecomm.modernized.order.application.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
    String productId, 
    int quantity, 
    BigDecimal unitPrice, 
    BigDecimal totalPrice
) {}
