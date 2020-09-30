package com.cs203t5.ryverbank.trading;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockServices{
    private StockRepository stocks;

    public List<Stock> listStocks() {
        return stocks.findAll();
    }
}
