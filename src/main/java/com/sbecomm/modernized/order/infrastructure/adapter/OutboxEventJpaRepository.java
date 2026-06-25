package com.sbecomm.modernized.order.infrastructure.adapter;

import com.sbecomm.modernized.order.infrastructure.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, String> {
    List<OutboxEventEntity> findByProcessedFalseOrderByCreatedAtAsc();
}
