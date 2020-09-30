package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Long>{
    //find all the transaction based on account id
    List<Transaction> findByFromOrTo(Long fromAccId, Long toAccId);
}