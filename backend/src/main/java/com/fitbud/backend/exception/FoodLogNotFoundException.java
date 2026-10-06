package com.fitbud.backend.exception;

public class FoodLogNotFoundException extends RuntimeException {

    public FoodLogNotFoundException(String message) {
        super(message);
    }
}