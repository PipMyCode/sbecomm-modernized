package com.sbecomm.modernized.catalog.domain.model;

public record ProductId(String value) {
    public ProductId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ProductId value cannot be null or empty");
        }
    }
}
