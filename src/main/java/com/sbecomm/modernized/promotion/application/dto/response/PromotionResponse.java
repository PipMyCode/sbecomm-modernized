package com.sbecomm.modernized.promotion.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionResponse(
        String id,
        String code,
        BigDecimal discountPercentage,
        boolean active,
        LocalDateTime expiresAt,
        Integer maxUses,
        Integer currentUses
) {
}
