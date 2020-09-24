package com.cs203t5.ryverbank.user;


public class UserExistsException extends RuntimeException {
    public UserExistsException(String error){
        super("Error: " + error);
    }
}
