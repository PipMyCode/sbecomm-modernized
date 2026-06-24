package com.sbecomm.modernized.order.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class OrderTest {

    @Test
    void shouldCreateOrderInCreatedState() {
        // Arrange & Act
        Order order = new Order(new OrderId("order-123"), "user-456", LocalDateTime.now(), OrderStatus.CREATED);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getItems()).isEmpty();
        assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldCalculateTotalAmountCorrectly() {
        // Arrange
        Order order = new Order(new OrderId("order-123"), "user-456", LocalDateTime.now(), OrderStatus.CREATED);
        
        // Act
        order.addOrderItem(new OrderItem("prod-1", 2, new BigDecimal("10.50"))); // 21.00
        order.addOrderItem(new OrderItem("prod-2", 1, new BigDecimal("20.00"))); // 20.00

        // Assert
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("41.00"));
    }

    @Test
    void shouldChangeStatus() {
        // Arrange
        Order order = new Order(new OrderId("order-123"), "user-456", LocalDateTime.now(), OrderStatus.CREATED);
        
        // Act
        order.markAsPaid();

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldRejectInvalidInitialization() {
        // Act & Assert
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Order(new OrderId(""), "user-456", LocalDateTime.now(), OrderStatus.CREATED))
                .withMessageContaining("OrderId cannot be null or blank");
                
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Order(new OrderId("order-123"), "", LocalDateTime.now(), OrderStatus.CREATED))
                .withMessageContaining("User ID cannot be blank");
    }
}
