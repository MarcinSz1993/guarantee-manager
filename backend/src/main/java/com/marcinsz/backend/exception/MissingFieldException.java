package com.marcinsz.backend.exception;

public class MissingFieldException extends RuntimeException{
    public MissingFieldException(String message){
        super(message);
    }
}
