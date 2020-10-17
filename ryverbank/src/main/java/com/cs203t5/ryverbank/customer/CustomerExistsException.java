package com.cs203t5.ryverbank.customer;

/**
 * CustomerExistsException will be thrown when a duplicate username is being
 * used to create a user.
 * 
 * @see RunTimeException
 */
public class CustomerExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CustomerExistsException(String error) {
        super("Error: " + error);
    }
}
