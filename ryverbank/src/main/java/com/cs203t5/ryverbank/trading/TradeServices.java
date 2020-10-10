package com.cs203t5.ryverbank.trading;
import com.cs203t5.ryverbank.customer.Customer;

import java.util.List;

public interface TradeServices {
    
    List <Trade> getAllTrades();
    Trade createMarketBuyTrade(Trade trade, Customer  customer, CustomStock customStock);
    Trade createMarketSellTrade(Trade trade, Customer  customer, CustomStock customStock);

    Trade getTrade(Long tradeId, Customer customer);
    Trade cancelTrade(Long tradeId, Customer customer);
    
    
}
