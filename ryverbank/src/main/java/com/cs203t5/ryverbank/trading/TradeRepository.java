package com.cs203t5.ryverbank.trading;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A TradeRepository that provides the mechanism for storage, retrieval,
 * search, update and delete operation on trade objects
 */
public interface TradeRepository extends JpaRepository <Trade, Long> {
    /**
     * Optional query to find the trade by the trade id.
     * 
     * @param id The id of the trade.
     * @return The trade found.
     */
    Optional<Trade> findById(Long id);

    // Optional<Trade> findBySymbol(String symbol);
    
    /**
     * Derived query to find all trades by the customer id.
     * 
     * @param id The id of the customer.
     * @return The list of all trades that belong to the customer id.
     */
    List<Trade> findAllByCustomerId(Long id);

    /**
     * Derived query to find all trades by the stock symbol.
     * 
     * @param symbol The stock symbol.
     * @return The list of all trades that has the stock symbol.
     */
    List<Trade> findAllBySymbol(String symbol);
}
