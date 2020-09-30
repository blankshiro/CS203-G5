package com.cs203t5.ryverbank.trading;

import com.cs203t5.ryverbank.account_transaction.AccountRepository;
import com.cs203t5.ryverbank.customer.CustomerRepository;

public class TradingController {
    private StockRepository stocks;
    private OrderRepository orders;
    private CustomerRepository users;
    private AccountRepository accounts;

    public TradingController(StockRepository stocks, OrderRepository orders, CustomerRepository users, AccountRepository accounts) {
        this.stocks = stocks;
        this.orders = orders;
        this.users = users;
        this.accounts = accounts;
    }

    
}