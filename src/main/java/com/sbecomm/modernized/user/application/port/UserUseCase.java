package com.sbecomm.modernized.user.application.port;

import com.sbecomm.modernized.user.application.dto.request.AddressRequest;
import com.sbecomm.modernized.user.application.dto.request.UpdateProfileRequest;
import com.sbecomm.modernized.user.application.dto.request.UserRegistrationRequest;
import com.sbecomm.modernized.user.application.dto.response.UserResponse;

public interface UserUseCase {
    UserResponse registerUser(UserRegistrationRequest request);
    UserResponse getUserProfile(String userId);
    UserResponse updateProfile(String userId, UpdateProfileRequest request);
    UserResponse addAddress(String userId, AddressRequest request);
    void removeAddress(String userId, AddressRequest request);
}
