package com.fitbud.backend.dto;

import java.time.LocalDateTime;

public record FoodResponse(
        Long id,
        String name,
        String brand,
        Double servingSizeGrams,
        Double calories,
        Double proteinGrams,
        Double carbohydrateGrams,
        Double fatGrams,
        LocalDateTime createdAt
) {
}