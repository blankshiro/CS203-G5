package com.cs203t5.ryverbank.portfolio;

import com.cs203t5.ryverbank.customer.Customer;
import com.cs203t5.ryverbank.trading.CustomStock;
import com.cs203t5.ryverbank.trading.Trade;

/**
 * An interface for various portfolio services.
 */
public interface PortfolioService {
    Portfolio getPortfolio(Long id);

    void calGainLoss(Portfolio portfolio);

    void updateRealizedGainLoss(Trade trade, CustomStock customStock);
}
