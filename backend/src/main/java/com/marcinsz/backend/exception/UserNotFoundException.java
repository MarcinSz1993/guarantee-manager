package com.marcinsz.backend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super(String.format("User with id %s not found", userId));
    }

    public UserNotFoundException(String userEmail) {
        super(String.format("User with email %s not found", userEmail));
    }
}
