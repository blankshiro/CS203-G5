package com.cs203t5.ryverbank.portfolio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository <Portfolio, Long> {
    Optional<Portfolio> findByCustomerId(Long id);
}
