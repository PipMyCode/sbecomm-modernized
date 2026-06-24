package com.sbecomm.modernized.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank(message = "Name is required") String name,
        String description,
        @NotNull(message = "Price is required") @PositiveOrZero(message = "Price cannot be negative") BigDecimal price,
        @NotBlank(message = "Category ID is required") String categoryId
) {
}
