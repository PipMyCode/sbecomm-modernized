package com.sbecomm.modernized.order.domain.repository;

import com.sbecomm.modernized.order.domain.model.Order;
import com.sbecomm.modernized.order.domain.model.OrderId;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(OrderId id);
    List<Order> findByUserId(String userId);
    Order save(Order order);
}
