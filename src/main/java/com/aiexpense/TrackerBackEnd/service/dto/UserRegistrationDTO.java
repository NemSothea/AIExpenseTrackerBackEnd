package com.aiexpense.trackerbackend.service.dto;

import jakarta.validation.constraints.*;

public record UserRegistrationDTO(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters long")
    String password,

    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be a valid 10-digit number")
    String contact,

    boolean enabled
) {}