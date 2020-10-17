package com.cs203t5.ryverbank.portfolio;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByCodeAndCustomerId(String symbol, Long id);
    List<Asset> findAllByCustomerId(Long id);
    List<Asset> findAllByCodeAndCustomerIdAndIsTraded(String symbol, Long id, String ans);
    //maybe should return back the asset and save it into another database?
    // void deleteByCodeAndCustomerId(String symbol, Long id);
}
