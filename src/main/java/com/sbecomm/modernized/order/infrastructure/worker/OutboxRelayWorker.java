package com.sbecomm.modernized.order.infrastructure.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbecomm.modernized.common.config.RabbitMQConfig;
import com.sbecomm.modernized.order.application.dto.event.OrderPlacedEvent;
import com.sbecomm.modernized.order.domain.model.OutboxEvent;
import com.sbecomm.modernized.order.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelayWorker {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void relayEvents() {
        List<OutboxEvent> events = outboxRepository.findUnprocessedEvents();
        if (!events.isEmpty()) {
            log.debug("Found {} unprocessed outbox events", events.size());
            
            for (OutboxEvent event : events) {
                try {
                    if ("OrderPlacedEvent".equals(event.getEventType())) {
                        OrderPlacedEvent payload = objectMapper.readValue(event.getPayload(), OrderPlacedEvent.class);
                        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ORDER_ROUTING_KEY, payload);
                        
                        log.info("Relayed event {} to RabbitMQ", event.getId());
                        
                        event.markProcessed();
                        outboxRepository.save(event);
                    }
                } catch (Exception e) {
                    log.error("Failed to relay outbox event {}", event.getId(), e);
                }
            }
        }
    }
}
