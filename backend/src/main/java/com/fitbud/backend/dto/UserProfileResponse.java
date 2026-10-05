package com.fitbud.backend.dto;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Double heightCm,
        Double weightKg,
        LocalDateTime createdAt
) {
}