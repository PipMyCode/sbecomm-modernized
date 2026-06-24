package com.sbecomm.modernized.user.domain.model;

public record UserId(String value) {
    public UserId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId value cannot be null or empty");
        }
    }
}
