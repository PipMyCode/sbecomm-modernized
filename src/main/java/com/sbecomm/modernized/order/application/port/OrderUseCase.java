package com.sbecomm.modernized.order.application.port;

import com.sbecomm.modernized.order.application.dto.request.CreateOrderRequest;
import com.sbecomm.modernized.order.application.dto.response.OrderResponse;
import java.util.List;

public interface OrderUseCase {
    OrderResponse createOrderFromCart(String userId, CreateOrderRequest request);
    OrderResponse getOrder(String userId, String orderId);
    List<OrderResponse> getUserOrders(String userId);
    OrderResponse cancelOrder(String userId, String orderId);
}
