package com.fitbud.backend.exception;

public class UserAccountNotFoundException extends RuntimeException {

    public UserAccountNotFoundException(String message) {
        super(message);
    }
}