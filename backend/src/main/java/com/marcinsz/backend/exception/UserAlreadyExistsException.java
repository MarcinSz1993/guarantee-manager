package com.marcinsz.backend.exception;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String username, String email) {
        super(String.format("User with username %s or email %s already exists!", username, email));
    }
}
