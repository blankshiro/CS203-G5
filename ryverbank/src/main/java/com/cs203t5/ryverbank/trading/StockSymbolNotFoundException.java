package com.cs203t5.ryverbank.trading;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StockSymbolNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public StockSymbolNotFoundException(String message){
        super(message);
    }
}
