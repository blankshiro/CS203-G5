package com.cs203t5.ryverbank.trading;

import org.springframework.stereotype.Service;

/**
 * Implementation of the StockServices class.
 * 
 * @see StockServices
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

    public CustomStock getStock(String symbol) {
        return stocks.findBySymbol(symbol).map(stock -> {
            return stocks.save(stock);
        }).orElse(null);
    }
}
