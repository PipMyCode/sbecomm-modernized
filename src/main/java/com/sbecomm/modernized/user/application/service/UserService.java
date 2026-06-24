package com.sbecomm.modernized.user.application.service;

import com.sbecomm.modernized.user.application.dto.request.AddressRequest;
import com.sbecomm.modernized.user.application.dto.request.UpdateProfileRequest;
import com.sbecomm.modernized.user.application.dto.request.UserRegistrationRequest;
import com.sbecomm.modernized.user.application.dto.response.AddressResponse;
import com.sbecomm.modernized.user.application.dto.response.UserResponse;
import com.sbecomm.modernized.user.application.port.UserUseCase;
import com.sbecomm.modernized.user.domain.model.Address;
import com.sbecomm.modernized.user.domain.model.User;
import com.sbecomm.modernized.user.domain.model.UserId;
import com.sbecomm.modernized.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.findById(new UserId(request.id())).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = new User(new UserId(request.id()), request.email(), request.firstName(), request.lastName());
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.updateProfile(request.firstName(), request.lastName());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse addAddress(String userId, AddressRequest request) {
        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Address address = new Address(
                request.street(), request.city(), request.state(),
                request.zipCode(), request.country(), request.isDefault()
        );
        user.addAddress(address);
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void removeAddress(String userId, AddressRequest request) {
        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Address address = new Address(
                request.street(), request.city(), request.state(),
                request.zipCode(), request.country(), request.isDefault()
        );
        user.removeAddress(address);
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        List<AddressResponse> addressResponses = user.getAddresses().stream()
                .map(a -> new AddressResponse(
                        a.getStreet(), a.getCity(), a.getState(),
                        a.getZipCode(), a.getCountry(), a.isDefault()))
                .collect(Collectors.toList());
        return new UserResponse(
                user.getId().value(), user.getEmail(), user.getFirstName(), user.getLastName(), addressResponses
        );
    }
}
