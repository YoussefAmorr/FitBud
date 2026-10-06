package com.fitbud.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateNutritionGoalRequest(

        @NotNull(message = "Calorie target is required")
        @Positive(message = "Calorie target must be greater than zero")
        Integer calorieTarget,

        @NotNull(message = "Protein target is required")
        @Positive(message = "Protein target must be greater than zero")
        Integer proteinTargetGrams,

        @NotNull(message = "Carbohydrate target is required")
        @Positive(message = "Carbohydrate target must be greater than zero")
        Integer carbohydrateTargetGrams,

        @NotNull(message = "Fat target is required")
        @Positive(message = "Fat target must be greater than zero")
        Integer fatTargetGrams

) {
}