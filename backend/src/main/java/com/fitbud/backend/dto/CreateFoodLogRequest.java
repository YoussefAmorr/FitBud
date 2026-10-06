package com.fitbud.backend.dto;

import com.fitbud.backend.model.MealType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateFoodLogRequest(

        @NotNull(message = "Food ID is required")
        Long foodId,

        @NotNull(message = "Meal type is required")
        MealType mealType,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        Double quantityGrams,

        @NotNull(message = "Eaten time is required")
        LocalDateTime eatenAt

) {
}