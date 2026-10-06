package com.fitbud.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateFoodRequest(

        @NotBlank(message = "Food name is required")
        String name,

        String brand,

        @NotNull(message = "Serving size is required")
        @Positive(message = "Serving size must be greater than zero")
        Double servingSizeGrams,

        @NotNull(message = "Calories are required")
        @PositiveOrZero(message = "Calories cannot be negative")
        Double calories,

        @NotNull(message = "Protein is required")
        @PositiveOrZero(message = "Protein cannot be negative")
        Double proteinGrams,

        @NotNull(message = "Carbohydrates are required")
        @PositiveOrZero(message = "Carbohydrates cannot be negative")
        Double carbohydrateGrams,

        @NotNull(message = "Fat is required")
        @PositiveOrZero(message = "Fat cannot be negative")
        Double fatGrams

) {
}