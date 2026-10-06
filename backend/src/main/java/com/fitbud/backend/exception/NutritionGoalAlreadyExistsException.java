package com.fitbud.backend.exception;

public class NutritionGoalAlreadyExistsException extends RuntimeException {

    public NutritionGoalAlreadyExistsException(String message) {
        super(message);
    }
}