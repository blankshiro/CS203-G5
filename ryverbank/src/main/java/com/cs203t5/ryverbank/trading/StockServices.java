package com.cs203t5.ryverbank.trading;

/**
 * An interface for various stock services.
 */
public interface StockServices {

    /**
     * Finds the stock based on the specified stock symbol. Return null if no stock
     * is found.
     * 
     * @param symbol The stock symbol.
     * @return The stock with the specified symbol.
     */
    CustomStock getStock(String symbol);
}
