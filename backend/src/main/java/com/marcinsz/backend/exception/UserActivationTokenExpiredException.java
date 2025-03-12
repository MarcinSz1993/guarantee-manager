package com.marcinsz.backend.exception;

public class UserActivationTokenExpiredException extends RuntimeException {
    public UserActivationTokenExpiredException() {
        super("User activation token is expired. A new token has been sent.");
    }
}
