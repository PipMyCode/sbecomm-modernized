package com.sbecomm.modernized.cart.application.port;

import com.sbecomm.modernized.cart.application.dto.request.AddCartItemRequest;
import com.sbecomm.modernized.cart.application.dto.request.UpdateCartItemRequest;
import com.sbecomm.modernized.cart.application.dto.response.CartResponse;

public interface CartUseCase {
    CartResponse getCart(String userId);

    CartResponse addItem(String userId, AddCartItemRequest request);

    CartResponse updateItemQuantity(String userId, String productId, UpdateCartItemRequest request);

    CartResponse removeItem(String userId, String productId);
    
    CartResponse applyPromotion(String userId, com.sbecomm.modernized.cart.application.dto.request.ApplyPromotionRequest request);
    
    CartResponse removePromotion(String userId);

    void clearCart(String userId);
}
