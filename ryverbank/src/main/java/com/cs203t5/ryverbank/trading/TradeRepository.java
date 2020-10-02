package com.cs203t5.ryverbank.trading;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TradeRepository extends JpaRepository <Trade, Long> {
    Optional<Trade> findById(Long id);
    Optional<Trade> findBySymbol(String symbol);
}
