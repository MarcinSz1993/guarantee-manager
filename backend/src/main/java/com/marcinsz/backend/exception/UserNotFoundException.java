package com.marcinsz.backend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super(String.format("User with id %s not found", userId));
    }

    public UserNotFoundException(String message){
        super(message);
    }

    public static UserNotFoundException byEmail(String userEmail) {
        return new UserNotFoundException(String.format("User with email %s not found", userEmail));
    }

    public static UserNotFoundException byUsername(String username) {
        return new UserNotFoundException(String.format("User with username %s not found", username));
    }


}
