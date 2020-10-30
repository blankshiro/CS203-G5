package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A TransactionRepository that provides the mechanism for storage, retrieval,
 * search, update and delete operation on transaction objects
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
     * Derived query to find all the transaction based on account id.
     * 
     * @param fromAccId From the specified account.
     * @param toAccId   To the specified account.
     * @return The list of transactions found between the accounts specified.
     */
    List<Transaction> findByAccount1OrAccount2(Long fromAccId, Long toAccId);
}