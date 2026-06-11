package org.pipmycode.sbecomm.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductDTO(
        Long productId,

        @NotBlank(message = "Product name cannot be blank")
        @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
        String productName,

        @NotBlank(message = "Product description cannot be blank")
        @Size(min = 10, message = "Product description must be at least 10 characters long")
        String productDescription,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,

        @NotNull(message = "Price cannot be null")
        @Min(value = 0, message = "Price cannot be negative")
        BigDecimal price,

        @Min(value = 0, message = "Discounted price cannot be negative")
        BigDecimal discountedPrice,

        String imageUrl,

        Long categoryId
){}
