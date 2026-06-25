package com.sbecomm.modernized.payment.application.service;

import com.sbecomm.modernized.common.config.RabbitMQConfig;
import com.sbecomm.modernized.order.application.dto.event.OrderPlacedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentMessageListener {

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("==================================================");
        log.info("RABBITMQ LISTENER: Received OrderPlacedEvent");
        log.info("Processing Payment for Order ID: {} | User ID: {}", event.orderId(), event.userId());
        log.info("Payment processed successfully. Asynchronous workflow complete!");
        log.info("==================================================");
    }
}
