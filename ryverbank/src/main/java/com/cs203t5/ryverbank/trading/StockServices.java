package com.cs203t5.ryverbank.trading;

import java.util.List;

public interface StockServices {
    List<CustomStock> listStocks();

    CustomStock getStock(String symbol);

    //CustomStock addStock(CustomStock stock);

    //CustomStock updateStock(String symbol, CustomStock stock);

    //void deleteStock(String symbol);

    //CustomStock createStock(CustomStock stock);
}
