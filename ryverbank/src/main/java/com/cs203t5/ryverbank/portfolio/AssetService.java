package com.cs203t5.ryverbank.portfolio;

import java.util.List;

import com.cs203t5.ryverbank.trading.CustomStock;
import com.cs203t5.ryverbank.trading.Trade;

public interface AssetService {
    void addAsset(Trade trade, CustomStock stock);
    // List<Asset> tradeAsset(String symbol, Long id);
    // List<Asset> getAssets(String symbol, Long id);
}
