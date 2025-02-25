package com.marcinsz.backend.exception;

public class IncorrectLoginOrPasswordException extends RuntimeException{
    public IncorrectLoginOrPasswordException(String message) {
        super(message);
    }
}
