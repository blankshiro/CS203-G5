package com.cs203t5.ryverbank.trading;

import java.util.List;

public interface StockServices {
    List<Stock> listStock();

    Stock getStock(String symbol);

    Stock addStock(Stock stock);

    Stock updateStock(String symbol, Stock stock);

    void deleteStock(String symbol);

    Stock createStock(Stock stock);
}
