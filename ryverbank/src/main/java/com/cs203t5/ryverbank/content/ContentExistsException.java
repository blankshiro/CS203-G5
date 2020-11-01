package com.cs203t5.ryverbank.content;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ContentExistsException will be thrown when the title of the content already
 * exists in the system.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ContentExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Throws the exception if the specified title exists.
     * 
     * @param title The title of the content that cannot be found.
     */
    public ContentExistsException(String title) {
        super("Error: Entered title" + title + "already exists");
    }

}
