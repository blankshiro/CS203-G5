package com.cs203t5.ryverbank.trading;

import java.util.List;


public interface StockServices {
    List<CustomStock> listStocks();

    CustomStock getStock(String symbol);


}
