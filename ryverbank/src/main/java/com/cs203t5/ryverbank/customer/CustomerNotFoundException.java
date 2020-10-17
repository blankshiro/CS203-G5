package com.cs203t5.ryverbank.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CustomerNotFoundException will be thrown when the specified customer cannot
 * be found in the repository.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Throws this exception if the id of the specified user cannot be found in the
     * repository.
     * 
     * @param id The id of the customer.
     */
    public CustomerNotFoundException(Long id) {
        super("Could not find user " + id);
    }

    /**
     * Throws this exception if the customer does not exist in the repository.
     * 
     * @param message The message to be printed.
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
