package com.fitbud.backend.dto;

public record LoginResponse(
        String token,
        String tokenType
) {
}