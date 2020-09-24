package com.cs203t5.ryverbank.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TransactionNotFoundException(Long id){
        super("Could not find transaction " + id);
    }
}
