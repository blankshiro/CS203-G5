package com.cs203t5.ryverbank.trading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TradeInvalidException will be thrown when a trade is invalid.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TradeInvalidException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new TradeInvalidException with the specified detail message.
     * 
     * @param error The message to be printed.
     */
    public TradeInvalidException(String error){
        super("Error: " + error);
    }
}