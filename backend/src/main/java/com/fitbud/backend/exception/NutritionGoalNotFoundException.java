package com.fitbud.backend.exception;

public class NutritionGoalNotFoundException extends RuntimeException {

    public NutritionGoalNotFoundException(String message) {
        super(message);
    }
}