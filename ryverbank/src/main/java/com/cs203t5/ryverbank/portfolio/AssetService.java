package com.cs203t5.ryverbank.portfolio;

import com.cs203t5.ryverbank.trading.CustomStock;
import com.cs203t5.ryverbank.trading.Trade;

public interface AssetService {
    void addAsset(Trade trade, CustomStock stock);
    // List<Asset> tradeAsset(String symbol, Long id);
    // List<Asset> getAssets(String symbol, Long id);
    void updateAsset(Trade trade, Asset asset, CustomStock stock, Portfolio portfolio);
    void sellAsset(String symbol, int quantity, Long customerId);
    void retrieveAsset(String symbol, int quantity, Long customerId);
}
