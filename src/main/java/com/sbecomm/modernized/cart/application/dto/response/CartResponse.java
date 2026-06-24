package com.sbecomm.modernized.cart.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        String userId,
        List<CartItemResponse> items,
        BigDecimal subTotal,
        String appliedPromotionCode,
        BigDecimal discountPercentage,
        BigDecimal discountAmount,
        BigDecimal totalAmount
) {
}
