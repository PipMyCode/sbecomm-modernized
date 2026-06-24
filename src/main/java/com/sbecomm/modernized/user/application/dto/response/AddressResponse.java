package com.sbecomm.modernized.user.application.dto.response;

public record AddressResponse(
    String street,
    String city,
    String state,
    String zipCode,
    String country,
    boolean isDefault
) {}
