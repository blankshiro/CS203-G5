package com.cs203t5.ryverbank.portfolio;

public interface PortfolioService {
    Portfolio getPortfolio(Long id);
    void calGainLoss(Portfolio portfolio);
    // void addAsset(Trade trade);
    // void deleteAsset(String symbol, Long id);
}
