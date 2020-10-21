package com.cs203t5.ryverbank.portfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SellQuantityExceedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SellQuantityExceedException(String message){
        super("Your sell quantity exceeded the quantity of the asset you owned," + message);
    }
}
