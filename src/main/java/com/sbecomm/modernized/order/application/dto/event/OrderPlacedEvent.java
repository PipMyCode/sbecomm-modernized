package com.sbecomm.modernized.order.application.dto.event;

public record OrderPlacedEvent(String orderId, String userId) {}
