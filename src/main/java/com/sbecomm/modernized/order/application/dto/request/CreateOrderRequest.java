package com.sbecomm.modernized.order.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
    @NotBlank(message = "Shipping address ID is required") String shippingAddressId,
    @NotBlank(message = "Billing address ID is required") String billingAddressId
) {}
