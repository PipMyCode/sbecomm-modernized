package com.sbecomm.modernized.cart.infrastructure.adapter;

import com.sbecomm.modernized.cart.domain.model.Cart;
import com.sbecomm.modernized.cart.domain.model.CartItem;
import com.sbecomm.modernized.cart.domain.repository.CartRepository;
import com.sbecomm.modernized.catalog.domain.model.ProductId;
import com.sbecomm.modernized.user.domain.model.UserId;
import com.sbecomm.modernized.cart.infrastructure.entity.CartEntity;
import com.sbecomm.modernized.cart.infrastructure.entity.CartItemEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CartRepositoryAdapter implements CartRepository {

    private final CartJpaRepository jpaRepository;

    public CartRepositoryAdapter(CartJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Cart> findByUserId(UserId userId) {
        return jpaRepository.findById(userId.value()).map(this::toDomain);
    }

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = toEntity(cart);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteByUserId(UserId userId) {
        jpaRepository.deleteById(userId.value());
    }

    private Cart toDomain(CartEntity entity) {
        Cart cart = new Cart(new UserId(entity.getUserId()));
        if (entity.getItems() != null) {
            entity.getItems().forEach(itemEntity -> 
                cart.addItem(new ProductId(itemEntity.getProductId()), itemEntity.getQuantity(), itemEntity.getUnitPrice())
            );
        }
        if (entity.getAppliedPromotionCode() != null) {
            cart.applyPromotion(entity.getAppliedPromotionCode(), entity.getDiscountPercentage());
        }
        return cart;
    }

    private CartEntity toEntity(Cart cart) {
        CartEntity entity = new CartEntity();
        entity.setUserId(cart.getUserId().value());
        
        if (cart.getItems() != null) {
            entity.setItems(cart.getItems().stream().map(domainItem -> {
                CartItemEntity itemEntity = new CartItemEntity();
                itemEntity.setCart(entity);
                itemEntity.setProductId(domainItem.getProductId().value());
                itemEntity.setQuantity(domainItem.getQuantity());
                itemEntity.setUnitPrice(domainItem.getUnitPrice());
                return itemEntity;
            }).collect(Collectors.toList()));
        }
        
        entity.setAppliedPromotionCode(cart.getAppliedPromotionCode());
        entity.setDiscountPercentage(cart.getDiscountPercentage());
        
        return entity;
    }
}
