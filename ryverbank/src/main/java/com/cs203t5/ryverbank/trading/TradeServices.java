package com.cs203t5.ryverbank.trading;

import com.cs203t5.ryverbank.customer.Customer;

import java.util.List;

/**
 * An interface for various trade services.
 */
public interface TradeServices {

    // List<Trade> getAllTrades();

    /**
     * Creates a market buy trade with the specified trade, customer and stock.
     * 
     * @param trade       The trade to be made.
     * @param customer    The customer that wants to market buy.
     * @param customStock The stock that the customer wants to buy.
     * @return The market buy trade created.
     */
    Trade createMarketBuyTrade(Trade trade, Customer customer, CustomStock customStock);

    /**
     * Creates a market sell trade with the specified trade, customer and stock.
     * 
     * @param trade       The trade to be made.
     * @param customer    The customer that wants to market sell.
     * @param customStock The stock that the customer wants to sell.
     * @return The market sell trade created.
     */
    Trade createMarketSellTrade(Trade trade, Customer customer, CustomStock customStock);

    /**
     * Creates a limit buy trade with the specified trade, customer and stock.
     * 
     * @param trade       The trade to be made.
     * @param customer    The customer that wants to limit buy.
     * @param customStock The stock that the customer wants to buy.
     * @return The limit buy trade created.
     */
    Trade createLimitBuyTrade(Trade trade, Customer customer, CustomStock customStock);

    /**
     * Creates a limit sell trade with the specified trade, customer and stock.
     * 
     * @param trade       The trade to be made.
     * @param customer    The customer that wants to limit sell.
     * @param customStock The stock that the customer wants to sell.
     * @return The limit sell trade created.
     */
    Trade createLimitSellTrade(Trade trade, Customer customer, CustomStock customStock);

    /**
     * Finds the trade with the specified trade id and customer. This method can
     * only be accessed by the customer who created the trade. Otherwise, it will
     * throw a CustomerUnauthorizedException.
     * 
     * @param customer The customer that created the trade.
     * @param tradeId  The trade id.
     * @return The trade found.
     */
    Trade getTrade(Long tradeId, Customer customer);

    /**
     * Cancels a trade with the specified trade id and customer. This method can
     * only be accessed by the customer who wants to cancel the trade. Otherwise, it
     * will throw a CustomerUnauthorizedException.
     * 
     * This method can only be called when the customer wants to cancel an open
     * trade. Otherwise, it will throw a TradeInvalidException.
     * 
     * @param tradeId  The trade id.
     * @param customer The customer that wants to cancel the trade.
     * @return The trade cancelled.
     */
    Trade cancelTrade(Long tradeId, Customer customer);

}
