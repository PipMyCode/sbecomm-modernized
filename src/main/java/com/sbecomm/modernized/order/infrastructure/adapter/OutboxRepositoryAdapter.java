package com.sbecomm.modernized.order.infrastructure.adapter;

import com.sbecomm.modernized.order.domain.model.OutboxEvent;
import com.sbecomm.modernized.order.domain.repository.OutboxRepository;
import com.sbecomm.modernized.order.infrastructure.entity.OutboxEventEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OutboxRepositoryAdapter implements OutboxRepository {

    private final OutboxEventJpaRepository repository;

    public OutboxRepositoryAdapter(OutboxEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        OutboxEventEntity entity = new OutboxEventEntity(
                event.getId(),
                event.getAggregateType(),
                event.getAggregateId(),
                event.getEventType(),
                event.getPayload(),
                event.isProcessed(),
                event.getCreatedAt()
        );
        entity = repository.save(entity);
        return toDomain(entity);
    }

    @Override
    public List<OutboxEvent> findUnprocessedEvents() {
        return repository.findByProcessedFalseOrderByCreatedAtAsc().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private OutboxEvent toDomain(OutboxEventEntity entity) {
        return new OutboxEvent(
                entity.getId(),
                entity.getAggregateType(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getPayload(),
                entity.isProcessed(),
                entity.getCreatedAt()
        );
    }
}
