package com.cs203t5.ryverbank.content;

public class ContentExistsException extends RuntimeException{

    public ContentExistsException(String title){
        super("Error: Entered title" + title + "already exists");
    }
    
}
