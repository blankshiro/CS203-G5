package com.cs203t5.ryverbank.trading;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StockServiceImpl implements StockServices {
    private StockRepository stocks;

    public StockServiceImpl (StockRepository stocks) {
        this.stocks = stocks;
    }

    public List<CustomStock> listStocks() {
        return stocks.findAll();
    }

    public CustomStock getStock(String symbol) {
        return stocks.findBySymbol(symbol).map(stock -> 
        {
            return stocks.save(stock);
        }).orElse(null);
    }
}
