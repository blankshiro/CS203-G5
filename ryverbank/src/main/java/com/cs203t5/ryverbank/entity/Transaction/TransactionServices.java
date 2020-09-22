package com.cs203t5.ryverbank.entity.Transaction;

import java.util.List;

public interface TransactionServices {

    List<Transaction> listTransactions();

    Transaction getTransaction(Long id);

    Transaction addTransaction(Transaction transaction);

    Transaction updateTransaction(Long id, Transaction transaction);

    void deleteTransaction(Long id);

}
