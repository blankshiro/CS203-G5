package com.cs203t5.ryverbank.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CustomerNotFoundException will be thrown when the specified customer cannot
 * be found in the repository.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new CustomerNotFoundException with the specified id.
     * 
     * @param id The id of the customer that cannot be found.
     */
    public CustomerNotFoundException(Long id) {
        super("Could not find user " + id);
    }

    /**
     * Constructs a new CustomerNotFoundException with the specified detail message.
     * 
     * @param message The message to be printed.
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
