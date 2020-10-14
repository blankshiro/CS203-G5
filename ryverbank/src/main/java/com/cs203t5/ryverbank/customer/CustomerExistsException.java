package com.cs203t5.ryverbank.customer;


public class CustomerExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public CustomerExistsException(String error){
        super("Error: " + error);
    }
}
