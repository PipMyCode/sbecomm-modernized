package com.sbecomm.modernized.cart.domain.repository;

import com.sbecomm.modernized.cart.domain.model.Cart;
import com.sbecomm.modernized.user.domain.model.UserId;
import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findByUserId(UserId userId);
    Cart save(Cart cart);
    void deleteByUserId(UserId userId);
}
