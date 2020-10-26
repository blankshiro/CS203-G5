package com.cs203t5.ryverbank.account_transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InsufficientBalanceException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String error){
        super("Error: " + error);
    }
}
