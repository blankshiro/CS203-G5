package com.cs203t5.ryverbank.service;

import com.cs203t5.ryverbank.entity.*;
import java.util.ArrayList;

public interface StockTransactionServices {
    ArrayList<StockTransaction> listStockCart();
    void addStockTransaction(StockTransaction transaction);
    StockTransaction createStockTransaction(Stock stock);

}
