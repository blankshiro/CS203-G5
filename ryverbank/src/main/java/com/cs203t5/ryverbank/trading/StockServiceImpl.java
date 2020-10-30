package com.cs203t5.ryverbank.trading;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of the StockServices class.
 * 
 * @see StockServices.
 */
@Service
public class StockServiceImpl implements StockServices {
    /** The stock repository. */
    private StockRepository stocks;

    /**
     * Constructs a StockServiceImpl with the following parameter.
     * 
     * @param stocks The stock repository.
     */
    public StockServiceImpl(StockRepository stocks) {
        this.stocks = stocks;
    }

    // public List<CustomStock> listStocks() {
    // return stocks.findAll();
    // }

    /**
     * Finds the stock based on the specified stock symbol. Return null if no stock
     * is found.
     * 
     * @param symbol The stock symbol.
     */
    public CustomStock getStock(String symbol) {
        return stocks.findBySymbol(symbol).map(stock -> {
            return stocks.save(stock);
        }).orElse(null);
    }
}
