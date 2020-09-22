package com.cs203t5.ryverbank.entity.User;

public class UserAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException() {
        super("Email has already been used" );
    }
}
