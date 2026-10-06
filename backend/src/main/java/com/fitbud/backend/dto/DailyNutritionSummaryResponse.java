package com.fitbud.backend.dto;

import java.time.LocalDate;

public record DailyNutritionSummaryResponse(
        Long userProfileId,
        LocalDate date,

        Double caloriesConsumed,
        Integer calorieTarget,
        Double caloriesRemaining,

        Double proteinConsumedGrams,
        Integer proteinTargetGrams,
        Double proteinRemainingGrams,

        Double carbohydrateConsumedGrams,
        Integer carbohydrateTargetGrams,
        Double carbohydrateRemainingGrams,

        Double fatConsumedGrams,
        Integer fatTargetGrams,
        Double fatRemainingGrams
) {
}