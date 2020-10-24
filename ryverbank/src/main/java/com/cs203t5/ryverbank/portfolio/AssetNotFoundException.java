package com.cs203t5.ryverbank.portfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AssetNotFoundException will be thrown when the specified asset cannot be
 * found in the repository.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AssetNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new AssetNotFoundException with the specified detail message.
     * 
     * @param message The message to be printed.
     */
    public AssetNotFoundException(String message) {
        super(message);
    }
}
