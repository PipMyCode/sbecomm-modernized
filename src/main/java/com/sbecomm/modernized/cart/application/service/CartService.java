package com.sbecomm.modernized.cart.application.service;

import com.sbecomm.modernized.cart.application.dto.request.AddCartItemRequest;
import com.sbecomm.modernized.cart.application.dto.request.UpdateCartItemRequest;
import com.sbecomm.modernized.cart.application.dto.response.CartItemResponse;
import com.sbecomm.modernized.cart.application.dto.response.CartResponse;
import com.sbecomm.modernized.cart.application.port.CartUseCase;
import com.sbecomm.modernized.cart.domain.model.Cart;
import com.sbecomm.modernized.cart.domain.model.CartItem;
import com.sbecomm.modernized.cart.domain.repository.CartRepository;
import com.sbecomm.modernized.catalog.application.dto.response.ProductResponse;
import com.sbecomm.modernized.catalog.application.port.CatalogUseCase;
import com.sbecomm.modernized.promotion.application.port.PromotionUseCase;
import com.sbecomm.modernized.promotion.application.dto.response.PromotionResponse;
import com.sbecomm.modernized.catalog.domain.model.ProductId;
import com.sbecomm.modernized.user.domain.model.UserId;
import com.sbecomm.modernized.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService implements CartUseCase {

    private final CartRepository cartRepository;
    private final CatalogUseCase catalogUseCase;
    private final PromotionUseCase promotionUseCase;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        log.debug("Retrieving cart for userId: {}", userId);
        Cart cart = cartRepository.findByUserId(new UserId(userId)).orElseGet(() -> {
            log.debug("No existing cart found for userId: {}, returning a new empty cart", userId);
            return new Cart(new UserId(userId));
        });
        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(String userId, AddCartItemRequest request) {
        log.info("Processing request to add item to cart. userId: {}, productId: {}, quantity: {}", userId, request.productId(), request.quantity());
        Cart cart = cartRepository.findByUserId(new UserId(userId)).orElseGet(() -> new Cart(new UserId(userId)));

        // Fetch product to validate existence and get current price
        log.debug("Validating product details for productId: {}", request.productId());
        ProductResponse product = catalogUseCase.getProduct(request.productId());

        cart.addItem(new ProductId(product.id()), request.quantity(), product.price());
        log.debug("Item added to cart domain model. Saving cart...");
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(String userId, String productId, UpdateCartItemRequest request) {
        log.info("Processing request to update cart item quantity. userId: {}, productId: {}, new quantity: {}", userId, productId, request.quantity());
        Cart cart = cartRepository.findByUserId(new UserId(userId))
                .orElseThrow(() -> {
                    log.error("Cart not found for userId: {}", userId);
                    return new IllegalArgumentException("Cart not found");
                });
        cart.updateItemQuantity(new ProductId(productId), request.quantity());
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeItem(String userId, String productId) {
        log.info("Removing item {} from cart for user {}", productId, userId);
        Cart cart = getOrCreateCart(new UserId(userId));
        cart.removeItem(new ProductId(productId));
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse applyPromotion(String userId, com.sbecomm.modernized.cart.application.dto.request.ApplyPromotionRequest request) {
        log.info("Applying promotion code {} to cart for user {}", request.code(), userId);
        Cart cart = getOrCreateCart(new UserId(userId));
        
        PromotionResponse promotion = promotionUseCase.validateCode(request.code())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or inactive promotion code: " + request.code()));
                
        cart.applyPromotion(promotion.code(), promotion.discountPercentage());
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removePromotion(String userId) {
        log.info("Removing promotion from cart for user {}", userId);
        Cart cart = getOrCreateCart(new UserId(userId));
        cart.removePromotion();
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void clearCart(String userId) {
        log.info("Processing request to clear cart for userId: {}", userId);
        cartRepository.findByUserId(new UserId(userId)).ifPresentOrElse(cart -> {
            cart.clear();
            cartRepository.save(cart);
            log.debug("Cart cleared and saved for userId: {}", userId);
        }, () -> log.debug("No cart found to clear for userId: {}", userId));
    }

    private Cart getOrCreateCart(UserId userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> new Cart(userId));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getProductId().value(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getUserId().value(),
                itemResponses,
                cart.getSubTotal(),
                cart.getAppliedPromotionCode(),
                cart.getDiscountPercentage(),
                cart.getDiscountAmount(),
                cart.getTotalAmount()
        );
    }
}
