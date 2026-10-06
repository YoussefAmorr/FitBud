package com.fitbud.backend.dto;

import java.time.LocalDateTime;

public record RegisterResponse(
        Long id,
        String email,
        LocalDateTime createdAt
) {
}