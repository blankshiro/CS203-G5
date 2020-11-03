package com.cs203t5.ryverbank.account_transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AccountCustomerNotFoundException will be thrown when the account cannot be
 * created because the user cannot be found.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountCustomerNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new AccountCustomerNotFoundException with the specified customer id.
     * 
     * @param id The customer id.
     */
    public AccountCustomerNotFoundException(Long id) {
        super("Bad request for creating account: Could not find user " + id);
    }

}
