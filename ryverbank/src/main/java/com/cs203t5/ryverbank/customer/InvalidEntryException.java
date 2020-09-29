package com.cs203t5.ryverbank.customer;

public class InvalidEntryException extends RuntimeException{
    public InvalidEntryException(String error){
        super("Error: " + error);
    }
}
