package com.sbecomm.modernized.order.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEventEntity {
    @Id
    private String id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    
    @Column(length = 4000)
    private String payload;
    private boolean processed;
    private LocalDateTime createdAt;
}
