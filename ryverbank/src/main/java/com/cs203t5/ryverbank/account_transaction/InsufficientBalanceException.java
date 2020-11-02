package com.cs203t5.ryverbank.account_transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * InsufficientBalanceException will be thrown when the specified account has
 * insufficient balance.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientBalanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InsufficientBalanceException with the specified detail message.
     * 
     * @param error The message to be printed.
     */
    public InsufficientBalanceException(String error) {
        super("Error: " + error);
    }
}
