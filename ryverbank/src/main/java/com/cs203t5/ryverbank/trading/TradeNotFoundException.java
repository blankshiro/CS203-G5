package com.cs203t5.ryverbank.trading;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradeNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public TradeNotFoundException(Long tradeId){
        super("Trade no. " + tradeId + " not found");
    }
}
