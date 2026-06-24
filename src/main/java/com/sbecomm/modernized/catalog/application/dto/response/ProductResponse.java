package com.sbecomm.modernized.catalog.application.dto.response;

import java.math.BigDecimal;

public record ProductResponse(String id,
                              String name,
                              String description,
                              BigDecimal price,
                              int stockQuantity,
                              CategoryResponse category) {
}
