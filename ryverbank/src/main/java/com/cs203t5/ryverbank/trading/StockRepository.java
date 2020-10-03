package com.cs203t5.ryverbank.trading;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface StockRepository extends JpaRepository <CustomStock, String>{
    Optional<CustomStock> findBySymbol(String symbol);

    

}
