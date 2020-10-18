package com.cs203t5.ryverbank.portfolio;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByCodeAndPortfolioId(String symbol, Long id);
    List<Asset> findAllByPortfolioId(Long id);
    List<Asset> findAllByCodeAndPortfolioIdAndIsTraded(String symbol, Long id, boolean ans);

    //maybe should return back the asset and save it into another database?
    // void deleteByCodeAndCustomerId(String symbol, Long id);
}
