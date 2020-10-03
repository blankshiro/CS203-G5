package com.cs203t5.ryverbank.trading;

import java.util.*;

public class StockServiceImpl implements StockServices {
    private StockRepository stocks;
    private StockCrawler crawler;

    public StockServiceImpl (StockRepository stocks, StockCrawler crawler) {
        this.stocks = stocks;
        this.crawler = crawler;
    }

    public List<CustomStock> listStocks() {
        return stocks.findAll();
    }

    public CustomStock getStock(String symbol) {
        Optional<CustomStock> optionalStock = stocks.findBySymbol(symbol);
        CustomStock stock = optionalStock.get();
        return stock;
    }
}
