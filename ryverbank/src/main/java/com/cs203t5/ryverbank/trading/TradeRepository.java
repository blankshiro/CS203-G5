package com.cs203t5.ryverbank.trading;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TradeRepository extends JpaRepository <Trade, Long> {
    Optional<Trade> findById(Long id);
    Optional<Trade> findBySymbol(String symbol);
    List<Trade> findAllByCustomerId(Long id);
    List<Trade> findAllBySymbol(String symbol);
}
