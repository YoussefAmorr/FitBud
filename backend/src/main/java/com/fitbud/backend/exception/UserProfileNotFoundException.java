package com.fitbud.backend.exception;

public class UserProfileNotFoundException extends RuntimeException {

    public UserProfileNotFoundException(String message) {
        super(message);
    }
}