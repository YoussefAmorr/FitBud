package com.fitbud.backend.exception;

public class UserProfileAlreadyExistsException extends RuntimeException {

    public UserProfileAlreadyExistsException(String message) {
        super(message);
    }
}