package com.cs203t5.ryverbank.portfolio;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface PortfolioRepository extends JpaRepository <Portfolio, Long> {
    Optional<Portfolio> findByCustomerId(Long id);
    @Modifying
    @Query(value = "DELETE FROM PORTFOLIO", nativeQuery = true)
    void deleteImmediate();
}
