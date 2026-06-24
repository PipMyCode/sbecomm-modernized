package com.sbecomm.modernized.cart.infrastructure.adapter;

import com.sbecomm.modernized.cart.infrastructure.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartJpaRepository extends JpaRepository<CartEntity, String> {
}
