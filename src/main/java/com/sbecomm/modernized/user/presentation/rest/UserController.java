package com.sbecomm.modernized.user.presentation.rest;

import com.sbecomm.modernized.user.application.dto.request.AddressRequest;
import com.sbecomm.modernized.user.application.dto.request.UpdateProfileRequest;
import com.sbecomm.modernized.user.application.dto.request.UserRegistrationRequest;
import com.sbecomm.modernized.user.application.dto.response.UserResponse;
import com.sbecomm.modernized.user.application.port.UserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;



    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> registerUser(
            @AuthenticationPrincipal Jwt jwt, 
            @Valid @RequestBody UserRegistrationRequest request) {
        // Enforce BOLA: Ensure token subject matches requested ID
        if (!jwt.getSubject().equals(request.id())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserResponse response = userUseCase.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userUseCase.getUserProfile(userId));
    }

    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable String userId, 
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userUseCase.updateProfile(userId, request));
    }

    @PostMapping("/{userId}/addresses")
    @PreAuthorize("#userId == authentication.name")
    public ResponseEntity<UserResponse> addAddress(
            @PathVariable String userId, 
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userUseCase.addAddress(userId, request));
    }

    @DeleteMapping("/{userId}/addresses")
    @PreAuthorize("#userId == authentication.name")
    public ResponseEntity<Void> removeAddress(
            @PathVariable String userId, 
            @Valid @RequestBody AddressRequest request) {
        userUseCase.removeAddress(userId, request);
        return ResponseEntity.noContent().build();
    }
}
