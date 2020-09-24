package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Long>{
    List<Transaction> findByUserId(Long userId);
    Optional<Transaction> findByidAndUserId(Long id, Long userId);
}