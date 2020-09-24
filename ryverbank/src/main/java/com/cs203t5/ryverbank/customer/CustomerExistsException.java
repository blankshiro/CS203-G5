package com.cs203t5.ryverbank.customer;


public class CustomerExistsException extends RuntimeException {
    public CustomerExistsException(String error){
        super("Error: " + error);
    }
}
