package com.cs203t5.ryverbank.account_transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AccountNotFoundException will be thrown when the account cannot be found.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new AccountNotFoundException with the specified account number.
     * 
     * @param accNumber The account number that cannot be found.
     */
    public AccountNotFoundException(Long accNumber){
        super("Could not find account " + accNumber);
    }

    /**
     * Constructs a new AccountNotFoundException with the specified detail message.
     * 
     * @param msg The message to be printed.
     */
    public AccountNotFoundException(String msg){
        super(msg);
    }
}   
