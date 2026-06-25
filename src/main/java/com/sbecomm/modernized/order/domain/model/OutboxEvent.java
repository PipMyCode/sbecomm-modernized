package com.sbecomm.modernized.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OutboxEvent {
    private String id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String payload;
    private boolean processed;
    private LocalDateTime createdAt;
    
    public void markProcessed() {
        this.processed = true;
    }
}
