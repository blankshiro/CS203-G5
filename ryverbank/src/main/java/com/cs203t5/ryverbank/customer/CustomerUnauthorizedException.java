package com.cs203t5.ryverbank.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CustomerUnauthorizedException will be thrown when a customer is not authorized to
 * perform a certain action.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class CustomerUnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new CustomerUnauthorizedException with the specified id.
     * 
     * @param id The id of the user that is unauthorized.
     */
    public CustomerUnauthorizedException(Long id) {
        super("Unauthorized access " + id);
    }

    /**
     * Constructs a new CustomerUnauthorizedException with the specified detail message.
     * 
     * @param message The message to be printed.
     */
    public CustomerUnauthorizedException(String message) {
        super(message);
    }
}
