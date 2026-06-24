package com.sbecomm.modernized.user.application.dto.response;

import java.util.List;

public record UserResponse(
    String id,
    String email,
    String firstName,
    String lastName,
    List<AddressResponse> addresses
) {}
