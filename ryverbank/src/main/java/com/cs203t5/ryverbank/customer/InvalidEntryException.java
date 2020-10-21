package com.cs203t5.ryverbank.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * InvalidEntryException is thrown when an invalid nric or phone number is used.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEntryException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InvalidEntryException with the specified detail message.
     * 
     * @param message The message to be printed.
     */
    public InvalidEntryException(String message) {
        super("Error: " + message);
    }
}