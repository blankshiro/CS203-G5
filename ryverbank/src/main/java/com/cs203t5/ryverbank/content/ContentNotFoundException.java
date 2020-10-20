package com.cs203t5.ryverbank.content;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * ContentNotFoundException will be thrown when the specified content cannot be
 * found in the system.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContentNotFoundException extends RuntimeException {

    /**
     * Constructs a new ContentNotFoundException with the specified id.
     * 
     * @param id The id of the content that cannot be found.
     */
    public ContentNotFoundException(Long id) {
        super("Unable to find content " + id);
    }

    /**
     * Constructs a new ContentNotFoundException with the specified detail message.
     * 
     * @param message The message to be printed.
     */
    public ContentNotFoundException(String message) {
        super(message);
    }
}
