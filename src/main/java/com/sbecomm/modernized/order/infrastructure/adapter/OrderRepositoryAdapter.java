package com.sbecomm.modernized.order.infrastructure.adapter;

import com.sbecomm.modernized.order.domain.model.Order;
import com.sbecomm.modernized.order.domain.model.OrderId;
import com.sbecomm.modernized.order.domain.model.OrderItem;
import com.sbecomm.modernized.order.domain.repository.OrderRepository;
import com.sbecomm.modernized.order.infrastructure.entity.OrderEntity;
import com.sbecomm.modernized.order.infrastructure.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        return toDomain(jpaRepository.save(entity));
    }

    private Order toDomain(OrderEntity entity) {
        Order order = new Order(new OrderId(entity.getId()), entity.getUserId(), entity.getCreatedAt(), entity.getStatus());
        if (entity.getItems() != null) {
            entity.getItems().forEach(itemEntity -> 
                order.addOrderItem(new OrderItem(itemEntity.getProductId(),
                        itemEntity.getQuantity(),
                        itemEntity.getUnitPrice()))
            );
        }
        if (entity.getAppliedPromotionCode() != null) {
            order.applyPromotion(entity.getAppliedPromotionCode(), entity.getDiscountPercentage());
        }
        return order;
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId().value());
        entity.setUserId(order.getUserId());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setStatus(order.getStatus());

        if (order.getItems() != null) {
            entity.setItems(order.getItems().stream().map(domainItem -> {
                OrderItemEntity itemEntity = new OrderItemEntity();
                itemEntity.setOrder(entity);
                itemEntity.setProductId(domainItem.getProductId());
                itemEntity.setQuantity(domainItem.getQuantity());
                itemEntity.setUnitPrice(domainItem.getUnitPrice());
                return itemEntity;
            }).collect(Collectors.toList()));
        }
        
        entity.setAppliedPromotionCode(order.getAppliedPromotionCode());
        entity.setDiscountPercentage(order.getDiscountPercentage());
        
        return entity;
    }
}
