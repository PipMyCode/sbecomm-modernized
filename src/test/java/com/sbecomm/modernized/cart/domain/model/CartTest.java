package com.sbecomm.modernized.cart.domain.model;

import com.sbecomm.modernized.catalog.domain.model.ProductId;
import com.sbecomm.modernized.user.domain.model.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {

    @Test
    void shouldCalculateTotalAmountCorrectly() {
        Cart cart = new Cart(new UserId("user-1"));

        cart.addItem(new ProductId("prod-1"), 2, new BigDecimal("10.00"));
        cart.addItem(new ProductId("prod-2"), 1, new BigDecimal("50.00"));

        // (2 * 10.00) + (1 * 50.00) = 70.00
        assertThat(cart.getTotalAmount()).isEqualByComparingTo(new BigDecimal("70.00"));
    }

    @Test
    void shouldIncrementQuantityForExistingProduct() {
        Cart cart = new Cart(new UserId("user-1"));
        ProductId productId = new ProductId("prod-1");

        cart.addItem(productId, 2, new BigDecimal("10.00"));
        cart.addItem(productId, 3, new BigDecimal("10.00")); // Adding 3 more of the same product

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
        assertThat(cart.getTotalAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void shouldRemoveItemFromCart() {
        Cart cart = new Cart(new UserId("user-1"));
        ProductId prod1 = new ProductId("prod-1");
        ProductId prod2 = new ProductId("prod-2");

        cart.addItem(prod1, 1, new BigDecimal("10.00"));
        cart.addItem(prod2, 1, new BigDecimal("20.00"));

        cart.removeItem(prod1);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getProductId()).isEqualTo(prod2);
        assertThat(cart.getTotalAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void shouldThrowExceptionWhenAddingNegativeQuantity() {
        Cart cart = new Cart(new UserId("user-1"));

        assertThatThrownBy(() -> cart.addItem(new ProductId("prod-1"), -1, new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");
    }
}
