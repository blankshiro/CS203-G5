package com.cs203t5.ryverbank.content;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContentNotFoundException extends RuntimeException{

    public ContentNotFoundException(Long id){
        super("Unable to find content " + id);
    }
    public ContentNotFoundException(String message){
        super(message);
    }
}
