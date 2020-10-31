package com.cs203t5.ryverbank.customer;

/**
 * CustomerExistsException will be thrown when a duplicate username is being
 * used to create a user.
 * 
 * @see RuntimeException
 */
public class CustomerExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new CustomerExistsException with the specified detail message.
     * 
     * @param message The message to be printed.
     */
    public CustomerExistsException(String message) {
        super("Error: " + message);
    }
}
