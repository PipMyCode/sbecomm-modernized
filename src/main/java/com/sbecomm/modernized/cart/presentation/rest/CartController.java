package com.sbecomm.modernized.cart.presentation.rest;

import com.sbecomm.modernized.cart.application.dto.request.AddCartItemRequest;
import com.sbecomm.modernized.cart.application.dto.request.UpdateCartItemRequest;
import com.sbecomm.modernized.cart.application.dto.response.CartResponse;
import com.sbecomm.modernized.cart.application.port.CartUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/carts/my-cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartUseCase cartUseCase;



    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> getMyCart(@AuthenticationPrincipal Jwt jwt) {
        // Enforce BOLA: The user's ID is the JWT Subject
        log.info("Fetching cart for user: {}", jwt.getSubject());
        return ResponseEntity.ok(cartUseCase.getCart(jwt.getSubject()));
    }

    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> addItemToCart(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddCartItemRequest request) {
        log.info("Adding item to cart for user: {}", jwt.getSubject());
        return ResponseEntity.ok(cartUseCase.addItem(jwt.getSubject(), request));
    }

    @PutMapping("/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        log.info("Updating item quantity in cart for user: {}, productId: {}", jwt.getSubject(), productId);
        return ResponseEntity.ok(cartUseCase.updateItemQuantity(jwt.getSubject(), productId, request));
    }

    @DeleteMapping("/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productId) {
        log.info("Removing item from cart for user: {}, productId: {}", jwt.getSubject(), productId);
        return ResponseEntity.ok(cartUseCase.removeItem(jwt.getSubject(), productId));
    }

    @PostMapping("/promotions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> applyPromotion(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody com.sbecomm.modernized.cart.application.dto.request.ApplyPromotionRequest request) {
        log.info("Applying promotion to cart for user: {}", jwt.getSubject());
        return ResponseEntity.ok(cartUseCase.applyPromotion(jwt.getSubject(), request));
    }

    @DeleteMapping("/promotions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> removePromotion(@AuthenticationPrincipal Jwt jwt) {
        log.info("Removing promotion from cart for user: {}", jwt.getSubject());
        return ResponseEntity.ok(cartUseCase.removePromotion(jwt.getSubject()));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal Jwt jwt) {
        log.info("Clearing cart for user: {}", jwt.getSubject());
        cartUseCase.clearCart(jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
