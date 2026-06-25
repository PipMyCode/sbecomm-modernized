package com.sbecomm.modernized.order.domain.repository;

import com.sbecomm.modernized.order.domain.model.OutboxEvent;
import java.util.List;

public interface OutboxRepository {
    OutboxEvent save(OutboxEvent event);
    List<OutboxEvent> findUnprocessedEvents();
}
