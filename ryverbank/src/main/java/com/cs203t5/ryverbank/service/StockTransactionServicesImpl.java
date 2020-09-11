package com.cs203t5.ryverbank.service;


import com.cs203t5.ryverbank.entity.*;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockTransactionServicesImpl implements StockTransactionServices{
    
    private ArrayList<StockTransaction> cartlist = new ArrayList<>();

    
    public StockTransactionServicesImpl(){
        //create dummy stock
        Stock stock1 = new Stock(2, "Temasek shares", "Temasek holdings", 1983.00, 45.00, 40.00);
        Stock stock2 = new Stock(3, "ST shares", "ST kinectics", 263, 1, 20);
        Stock stock3 = new Stock(4, "MapleTree Stock", "MapleTree holdings", 5023.56, 23.56, 89.9);

        cartlist.add(new StockTransaction(2, "188-98-76-5", "Ilovedoggies", "buy", 100.56, stock2, 1.23));
        cartlist.add(new StockTransaction(2, "188-98-76-5", "Ilovedoggies", "buy", 100.56, stock3, 1.23));
        StockTransaction purchase = createStockTransaction(stock1);
        cartlist.add(purchase);
    }

    public ArrayList<StockTransaction> listStockCart(){
        return cartlist;
    }


    //when press purchase newly transaction added into cartlist
    @Override
    public void addStockTransaction(StockTransaction transaction){
        cartlist.add(transaction);
    }

    @Override
    public StockTransaction createStockTransaction(Stock stock){
        StockTransaction transaction = new StockTransaction(1, "188-98-76-5", "Ilovedoggies", "buy", 100.56, stock, 1.23);
        addStockTransaction(transaction);
        return transaction;
    }


}
