package com.cs203t5.ryverbank.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public CustomerNotFoundException(Long id){
        super("Could not find user " + id);
    }

    public CustomerNotFoundException(String message){
        super(message);
    }
}
