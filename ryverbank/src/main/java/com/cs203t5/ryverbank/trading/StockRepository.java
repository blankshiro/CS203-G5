package com.cs203t5.ryverbank.trading;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A StockRepository that provides the mechanism for storage, retrieval,
 * search, update and delete operation on stock objects
 */
public interface StockRepository extends JpaRepository<CustomStock, String> {
    
    /**
     * Optional query to find stock by symbol.
     * 
     * @param symbol The symbol of the stock.
     * @return The stock found.
     */
    Optional<CustomStock> findBySymbol(String symbol);

}
