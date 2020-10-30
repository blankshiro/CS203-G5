package com.cs203t5.ryverbank.portfolio;

import com.cs203t5.ryverbank.trading.CustomStock;
import com.cs203t5.ryverbank.trading.Trade;

/**
 * An interface for various asset services.
 */
public interface AssetService {
    void addAsset(Trade trade, CustomStock stock);

    void updateAsset(Trade trade, Asset asset, CustomStock stock, Portfolio portfolio);

    void sellAsset(String symbol, int quantity, Long customerId);

    void retrieveAsset(String symbol, int quantity, Long customerId);
}
