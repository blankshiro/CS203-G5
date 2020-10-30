package com.cs203t5.ryverbank.trading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * StockSymbolNotFoundException is thrown when a stock symbol cannot be found.
 * 
 * @see RuntimeException.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StockSymbolNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new StockSymbolNotFoundException with the specified detail
     * message.
     * 
     * @param message The message to be printed.
     */
    public StockSymbolNotFoundException(String message) {
        super(message);
    }
}
