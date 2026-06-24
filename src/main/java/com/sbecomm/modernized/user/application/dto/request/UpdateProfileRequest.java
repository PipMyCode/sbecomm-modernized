package com.sbecomm.modernized.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @NotBlank(message = "First name is required") 
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,
    
    @NotBlank(message = "Last name is required") 
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName
) {}
