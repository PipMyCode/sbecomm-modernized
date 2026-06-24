package com.sbecomm.modernized.cart.infrastructure.adapter;

import com.sbecomm.modernized.cart.domain.model.Cart;
import com.sbecomm.modernized.cart.domain.model.CartItem;
import com.sbecomm.modernized.catalog.domain.model.ProductId;
import com.sbecomm.modernized.common.BaseIntegrationTest;
import com.sbecomm.modernized.user.domain.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.transaction.annotation.Transactional;

@Transactional
class CartRepositoryIT extends BaseIntegrationTest {

    @Autowired
    private CartRepositoryAdapter cartRepositoryAdapter;

    @Test
    void shouldSaveAndRetrieveCartWithStronglyTypedIds() {
        // Arrange
        UserId userId = new UserId("user-test-1");
        Cart cart = new Cart(userId);
        
        ProductId productId = new ProductId("prod-test-1");
        cart.addItem(productId, 2, new BigDecimal("15.50"));

        // Act
        cartRepositoryAdapter.save(cart);

        // Assert
        Optional<Cart> retrievedCart = cartRepositoryAdapter.findByUserId(userId);
        assertThat(retrievedCart).isPresent();
        assertThat(retrievedCart.get().getUserId()).isEqualTo(userId);
        assertThat(retrievedCart.get().getItems()).hasSize(1);
        
        CartItem item = retrievedCart.get().getItems().getFirst();
        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualByComparingTo(new BigDecimal("15.50"));
        assertThat(retrievedCart.get().getTotalAmount()).isEqualByComparingTo(new BigDecimal("31.00"));
    }

    @Test
    void shouldClearCartCorrectly() {
        // Arrange
        UserId userId = new UserId("user-test-clear");
        Cart cart = new Cart(userId);
        cart.addItem(new ProductId("prod-test-clear"), 1, new BigDecimal("10.00"));
        cartRepositoryAdapter.save(cart);

        // Act
        cartRepositoryAdapter.deleteByUserId(userId);

        // Assert
        Optional<Cart> deletedCart = cartRepositoryAdapter.findByUserId(userId);
        assertThat(deletedCart).isEmpty();
    }
}
