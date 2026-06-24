package com.sbecomm.modernized.promotion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Promotion {
    private String id;
    private String code;
    private BigDecimal discountPercentage;
    private boolean active;
    private LocalDateTime expiresAt;
    private Integer maxUses;
    private Integer currentUses;

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void consume() {
        if (!this.active) {
            throw new IllegalStateException("Promotion is not active");
        }
        if (this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt)) {
            throw new IllegalStateException("Promotion has expired");
        }
        if (this.maxUses != null && this.currentUses >= this.maxUses) {
            throw new IllegalStateException("Promotion usage limit reached");
        }
        this.currentUses++;
    }
}
