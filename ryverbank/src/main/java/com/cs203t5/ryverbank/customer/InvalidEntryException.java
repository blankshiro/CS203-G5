package com.cs203t5.ryverbank.customer;

@ResponseStatus(HttpStatus.INVALID)
public class InvalidEntryException extends RuntimeException{
    public InvalidEntryException(String error){
        super("Error: " + error);
    }

    public InvalidEntryException(String string) {
        super(string);
	}
}
