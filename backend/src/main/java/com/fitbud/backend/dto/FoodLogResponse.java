package com.fitbud.backend.dto;

import com.fitbud.backend.model.MealType;

import java.time.LocalDateTime;

public record FoodLogResponse(
        Long id,
        Long userProfileId,
        Long foodId,
        String foodName,
        String brand,
        MealType mealType,
        Double quantityGrams,
        Double calories,
        Double proteinGrams,
        Double carbohydrateGrams,
        Double fatGrams,
        LocalDateTime eatenAt,
        LocalDateTime createdAt
) {
}