package com.cs203t5.ryverbank.trading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TradeNotFoundException is thrown when a trade cannot be found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new TradeNotFoundException with the specified trade id.
     * 
     * @param tradeId The id of the trade that is not found.
     */
    public TradeNotFoundException(Long tradeId) {
        super("Trade no. " + tradeId + " not found");
    }
}
