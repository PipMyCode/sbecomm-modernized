package com.sbecomm.modernized.cart.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ApplyPromotionRequest(
    @NotBlank(message = "Promotion code is required") String code
) {}
