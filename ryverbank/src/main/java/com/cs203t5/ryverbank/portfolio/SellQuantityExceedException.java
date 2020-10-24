package com.cs203t5.ryverbank.portfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * SellQuantityException will be thrown when the sell quantity exceeds the
 * amount of asset that the user owns.
 * 
 * @see RuntimeException.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SellQuantityExceedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new AssetNotFoundException with the specified detail message.
     * 
     * @param message The message to be printed.
     */
    public SellQuantityExceedException(String message) {
        super("Your sell quantity exceeded the quantity of the asset you owned," + message);
    }
}
