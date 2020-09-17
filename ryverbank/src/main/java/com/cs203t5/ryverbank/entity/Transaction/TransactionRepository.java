package com.cs203t5.ryverbank.entity.Transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository <Transaction, Long>{
    List<Transaction> findByUserId(String userId);
    Optional<Transaction> findByIdAndUserId(Long id, String userId);
}