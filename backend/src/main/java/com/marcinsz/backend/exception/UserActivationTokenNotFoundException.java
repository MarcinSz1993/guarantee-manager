package com.marcinsz.backend.exception;

public class UserActivationTokenNotFoundException extends RuntimeException{
   public UserActivationTokenNotFoundException(String token){
        super("User activation token not found: " + token);
    }
}
