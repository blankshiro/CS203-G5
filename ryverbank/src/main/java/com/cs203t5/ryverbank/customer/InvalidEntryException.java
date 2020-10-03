package com.cs203t5.ryverbank.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEntryException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    public InvalidEntryException(String error){
        super("Error: " + error);
    }
}