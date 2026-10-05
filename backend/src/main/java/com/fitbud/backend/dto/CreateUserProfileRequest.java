package com.fitbud.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateUserProfileRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Positive(message = "Height must be greater than zero")
        Double heightCm,

        @Positive(message = "Weight must be greater than zero")
        Double weightKg

) {
}