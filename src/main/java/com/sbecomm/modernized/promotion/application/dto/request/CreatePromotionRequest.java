package com.sbecomm.modernized.promotion.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePromotionRequest(
        @NotBlank(message = "Promotion code is required") String code,
        @NotNull(message = "Discount percentage is required") @Min(1) @Max(100) BigDecimal discountPercentage,
        LocalDateTime expiresAt,
        @Min(1) Integer maxUses
) {
}
