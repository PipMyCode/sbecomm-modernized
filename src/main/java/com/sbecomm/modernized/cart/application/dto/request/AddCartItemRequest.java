package com.sbecomm.modernized.cart.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddCartItemRequest(
        @NotBlank(message = "Product ID is required") String productId,
        @Min(value = 1, message = "Quantity must be at least 1") int quantity
) {
}
