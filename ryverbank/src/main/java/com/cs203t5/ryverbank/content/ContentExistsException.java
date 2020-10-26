package com.cs203t5.ryverbank.content;

/**
 * ContentExistsException will be thrown when the title of the content already
 * exists in the system.
 * 
 * @see RuntimeException
 */
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
