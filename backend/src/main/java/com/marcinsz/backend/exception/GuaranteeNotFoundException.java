package com.marcinsz.backend.exception;

public class GuaranteeNotFoundException extends RuntimeException{
    public GuaranteeNotFoundException(String message){
        super(message);
    }
}
