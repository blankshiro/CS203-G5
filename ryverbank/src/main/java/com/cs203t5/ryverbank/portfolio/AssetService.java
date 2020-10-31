package com.cs203t5.ryverbank.portfolio;

import com.cs203t5.ryverbank.trading.CustomStock;
import com.cs203t5.ryverbank.trading.Trade;

/**
 * An interface for various asset services.
 */
public interface AssetService {
    /**
     * Creates an asset based on the specified trade and stock. This method will
     * only work if the trade is filled or partially filled.
     * 
     * @param trade The trade filled or partially filled.
     * @param stock The stock of the trade.
     */
    void addAsset(Trade trade, CustomStock stock);

    /**
     * Updates the asset based on the specified trade, asset, stock and portfolio.
     * 
     * @param trade     The updated trade.
     * @param asset     The asset to update.
     * @param stock     The stock of the trade.
     * @param portfolio The portfolio that contains the asset to update.
     */
    void updateAsset(Trade trade, Asset asset, CustomStock stock, Portfolio portfolio);

    /**
     * Sells an asset based on the specified symbol of asset, quantity amount and
     * customer id.
     * 
     * @param symbol     The symbol of the asset.
     * @param quantity   The quantity of the asset to sell.
     * @param customerId The customer id.
     */
    void sellAsset(String symbol, int quantity, Long customerId);

    /**
     * Retrieves back the asset with the specified asset symbol, quantity and
     * customer id when the customer cancels a trade.
     * 
     * @param symbol     The symbol of the asset.
     * @param quantity   The quantity of the asset cancelled.
     * @param customerId The customer id.
     */
    void retrieveAsset(String symbol, int quantity, Long customerId);
}
