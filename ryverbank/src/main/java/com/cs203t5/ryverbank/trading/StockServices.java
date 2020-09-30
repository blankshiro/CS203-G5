package com.cs203t5.ryverbank.trading;

import java.util.List;

import org.springframework.stereotype.Service;

public interface StockServices{
    List<Stock> listStocks();
}
