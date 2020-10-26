package com.cs203t5.ryverbank.account_transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TransactionNotFoundException is thrown when the specified transaction is not found.
 * THIS IS NOT USED ANYWHERE
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a TransactionNotFoundException with the specified transaction id.
     * 
     * @param id
     */
    public TransactionNotFoundException(Long id){
        super("Could not find transaction " + id);
    }
}
