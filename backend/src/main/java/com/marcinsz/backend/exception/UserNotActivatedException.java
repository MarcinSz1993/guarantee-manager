package com.marcinsz.backend.exception;

public class UserNotActivatedException extends RuntimeException{
    public UserNotActivatedException() {
        super("User is not activated");
    }
}
