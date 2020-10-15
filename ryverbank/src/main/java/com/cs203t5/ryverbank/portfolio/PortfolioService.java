package com.cs203t5.ryverbank.portfolio;

import com.cs203t5.ryverbank.customer.Customer;
import com.cs203t5.ryverbank.trading.Trade;

public interface PortfolioService {
    Portfolio getPortfolio(Long id);
    void addAsset(Trade trade, Long id);
    void deleteAsset(Trade trade, Long id);
}
