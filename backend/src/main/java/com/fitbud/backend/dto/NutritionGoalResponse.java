package com.fitbud.backend.dto;

import java.time.LocalDateTime;

public record NutritionGoalResponse(
        Long id,
        Long userProfileId,
        Integer calorieTarget,
        Integer proteinTargetGrams,
        Integer carbohydrateTargetGrams,
        Integer fatTargetGrams,
        LocalDateTime createdAt
) {
}