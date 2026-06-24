package com.sbecomm.modernized.order.infrastructure.adapter;

import com.sbecomm.modernized.order.domain.model.Order;
import com.sbecomm.modernized.order.domain.model.OrderId;
import com.sbecomm.modernized.order.domain.model.OrderItem;
import com.sbecomm.modernized.order.domain.model.OrderStatus;
import com.sbecomm.modernized.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import com.sbecomm.modernized.ModernizedEcommApplication;

import com.sbecomm.modernized.common.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OrderRepositoryIT extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndRetrieveOrderWithItems() {
        // Arrange: Pure Domain Aggregate
        Order order = new Order(new OrderId("order-100"), "user-1", LocalDateTime.now(), OrderStatus.CREATED);
        order.addOrderItem(new OrderItem("prod-1", 2, new BigDecimal("50.00")));
        order.addOrderItem(new OrderItem("prod-2", 1, new BigDecimal("25.00")));

        // Act: Persist through Outbound Adapter mapping to JPA Entities
        orderRepository.save(order);
        
        // Assert: Retrieve and verify full domain reconstruction
        Optional<Order> retrievedOrder = orderRepository.findById(new OrderId("order-100"));

        assertThat(retrievedOrder).isPresent();
        assertThat(retrievedOrder.get().getUserId()).isEqualTo("user-1");
        assertThat(retrievedOrder.get().getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(retrievedOrder.get().getItems()).hasSize(2);
        assertThat(retrievedOrder.get().getTotalAmount()).isEqualByComparingTo(new BigDecimal("125.00"));
    }

    @Test
    void shouldFindOrdersByUserId() {
        // Arrange
        Order order1 = new Order(new OrderId("order-101"), "user-2", LocalDateTime.now(), OrderStatus.CREATED);
        Order order2 = new Order(new OrderId("order-102"), "user-2", LocalDateTime.now(), OrderStatus.PAID);
        Order order3 = new Order(new OrderId("order-103"), "user-3", LocalDateTime.now(), OrderStatus.CREATED); // Different user
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);

        // Act
        List<Order> userOrders = orderRepository.findByUserId("user-2");

        // Assert: Verify database isolation and querying logic
        assertThat(userOrders).hasSize(2);
        assertThat(userOrders).extracting(Order::getId)
                .containsExactlyInAnyOrder(new OrderId("order-101"), new OrderId("order-102"));
    }
}
