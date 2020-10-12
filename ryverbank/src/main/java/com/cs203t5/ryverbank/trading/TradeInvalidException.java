package com.cs203t5.ryverbank.trading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TradeInvalidException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    public TradeInvalidException(String error){
        super("Error: " + error);
    }
}