package com.sbecomm.modernized.cart.application.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(String productId,
                               int quantity,
                               BigDecimal unitPrice,
                               BigDecimal totalPrice) {
}
