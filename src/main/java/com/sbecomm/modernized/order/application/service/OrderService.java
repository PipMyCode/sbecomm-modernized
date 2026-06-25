package com.sbecomm.modernized.order.application.service;

import com.sbecomm.modernized.cart.application.dto.response.CartItemResponse;
import com.sbecomm.modernized.cart.application.dto.response.CartResponse;
import com.sbecomm.modernized.cart.application.port.CartUseCase;
import com.sbecomm.modernized.catalog.application.port.CatalogUseCase;
import com.sbecomm.modernized.promotion.application.port.PromotionUseCase;
import com.sbecomm.modernized.order.application.dto.request.CreateOrderRequest;
import com.sbecomm.modernized.order.application.dto.response.OrderItemResponse;
import com.sbecomm.modernized.order.application.dto.response.OrderResponse;
import com.sbecomm.modernized.order.application.port.OrderUseCase;
import com.sbecomm.modernized.order.domain.model.Order;
import com.sbecomm.modernized.order.domain.model.OrderId;
import com.sbecomm.modernized.order.domain.model.OrderItem;
import com.sbecomm.modernized.order.domain.model.OrderStatus;
import com.sbecomm.modernized.order.domain.repository.OrderRepository;
import com.sbecomm.modernized.order.application.dto.event.OrderPlacedEvent;
import com.sbecomm.modernized.common.config.RabbitMQConfig;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;
import com.sbecomm.modernized.order.domain.repository.OutboxRepository;
import com.sbecomm.modernized.order.domain.model.OutboxEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final CartUseCase cartUseCase; // Integrating with another bounded context
    private final CatalogUseCase catalogUseCase; // Integrating with Catalog for inventory
    private final PromotionUseCase promotionUseCase;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;



    @Override
    @Transactional
    public OrderResponse createOrderFromCart(String userId, CreateOrderRequest request) {
        log.info("Initiating checkout process for userId: {}", userId);
        // Fetch cart via Inbound Port of Cart bounded context
        log.debug("Fetching cart for userId: {}", userId);
        CartResponse cart = cartUseCase.getCart(userId);
        
        if (cart.items() == null || cart.items().isEmpty()) {
            log.error("Checkout failed: Cart is empty for userId: {}", userId);
            throw new IllegalStateException("Cannot create order from an empty cart");
        }

        // Build a map of products and quantities to reserve
        java.util.Map<String, Integer> itemsToReserve = cart.items().stream()
                .collect(Collectors.toMap(CartItemResponse::productId, CartItemResponse::quantity));

        // Reserve inventory in the Catalog module before completing the order
        log.debug("Reserving inventory for {} unique items in the cart", itemsToReserve.size());
        catalogUseCase.reserveInventory(itemsToReserve);

        Order order = new Order(new OrderId(UUID.randomUUID().toString()), userId, LocalDateTime.now(), OrderStatus.CREATED);
        
        for (CartItemResponse cartItem : cart.items()) {
            // Snapshotting the price as it exists within the cart at this moment
            order.addOrderItem(new OrderItem(cartItem.productId(), cartItem.quantity(), cartItem.unitPrice()));
        }
        
        if (cart.appliedPromotionCode() != null) {
            order.applyPromotion(cart.appliedPromotionCode(), cart.discountPercentage());
            log.debug("Consuming promotion code: {}", cart.appliedPromotionCode());
            promotionUseCase.consumeCode(cart.appliedPromotionCode());
        }

        log.debug("Saving new order for userId: {}", userId);
        Order savedOrder = orderRepository.save(order);
        log.info("Order successfully created with orderId: {} for userId: {}", savedOrder.getId().value(), userId);
        
        // Clear the cart after successful order creation
        log.debug("Clearing cart for userId: {} after successful checkout", userId);
        cartUseCase.clearCart(userId);

        // Implement Transactional Outbox Pattern
        log.info("Saving OrderPlacedEvent to Outbox table for reliable async processing");
        OrderPlacedEvent event = new OrderPlacedEvent(savedOrder.getId().value(), userId);
        
        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = new OutboxEvent(
                    UUID.randomUUID().toString(),
                    "Order",
                    savedOrder.getId().value(),
                    "OrderPlacedEvent",
                    payload,
                    false,
                    LocalDateTime.now()
            );
            outboxRepository.save(outboxEvent);
        } catch (JacksonException e) {
            log.error("Failed to serialize OrderPlacedEvent for outbox", e);
            throw new RuntimeException("Failed to process order event", e);
        }

        return toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(String userId, String orderId) {
        log.debug("Fetching orderId: {} for userId: {}", orderId, userId);
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new IllegalArgumentException("Order not found");
                });
                
        // Ensure BOLA: Only the owner can view this order
        if (!order.getUserId().equals(userId)) {
            log.warn("Security violation: userId {} attempted to access order {} belonging to {}", userId, orderId, order.getUserId());
            throw new IllegalStateException("Order does not belong to the requested user");
        }
        
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String userId) {
        log.debug("Fetching all orders for userId: {}", userId);
        return orderRepository.findByUserId(userId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String userId, String orderId) {
        log.info("Initiating cancellation of orderId: {} for userId: {}", orderId, userId);
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> {
                    log.error("Order not found for cancellation: {}", orderId);
                    return new IllegalArgumentException("Order not found");
                });
                
        if (!order.getUserId().equals(userId)) {
            log.warn("Security violation: userId {} attempted to cancel order {} belonging to {}", userId, orderId, order.getUserId());
            throw new IllegalStateException("Order does not belong to the requested user");
        }
        
        order.cancel();
        log.info("Order successfully cancelled: {}", orderId);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getProductId(), i.getQuantity(), i.getUnitPrice(), i.getTotalPrice()))
                .collect(Collectors.toList());
                
        return new OrderResponse(
                order.getId().value(),
                order.getUserId(),
                order.getCreatedAt(),
                order.getStatus().name(),
                itemResponses,
                order.getSubTotal(),
                order.getAppliedPromotionCode(),
                order.getDiscountPercentage(),
                order.getDiscountAmount(),
                order.getTotalAmount()
        );
    }
}
