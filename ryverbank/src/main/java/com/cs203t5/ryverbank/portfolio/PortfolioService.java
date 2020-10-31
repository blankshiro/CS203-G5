package com.cs203t5.ryverbank.portfolio;

import com.cs203t5.ryverbank.trading.CustomStock;
import com.cs203t5.ryverbank.trading.Trade;

/**
 * An interface for various portfolio services.
 */
public interface PortfolioService {
    /**
     * Finds the portfolio with the specified customer id.
     * 
     * @param id The customer id.
     * @return The portfolio found
     */
    Portfolio getPortfolio(Long id);

    /**
     * Calculates the unrealized profit or loss for the assets owned based on the
     * specified portfolio.
     * 
     * @param portfolio The portfolio to calculate.
     */
    void calGainLoss(Portfolio portfolio);

    /**
     * Updates the realized gain or loss based on the specified trade and stock.
     * 
     * @param trade The trade to calculate.
     * @param customStock The stock being traded.
     */
    void updateRealizedGainLoss(Trade trade, CustomStock customStock);
}
