package com.cs203t5.ryverbank.portfolio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A PortfolioRepository that provides the mechanism for storage, retrieval,
 * search, update and delete operation on portfolio objects
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    /**
     * Optional query to find portfolio by customer id.
     * 
     * @param id The id of the customer.
     * @return The portfolio found
     */
    Optional<Portfolio> findByCustomerId(Long id);
}
