package com.cs203t5.ryverbank.portfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AssetNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AssetNotFoundException(String message){
        super(message);
    }
}
