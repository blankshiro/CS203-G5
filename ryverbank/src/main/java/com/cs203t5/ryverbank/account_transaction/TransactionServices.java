package com.cs203t5.ryverbank.account_transaction;

import java.util.List;

public interface TransactionServices {

    List<Transaction> listTransactions();

    Transaction getTransaction(Long id);

    Transaction addTransaction(Transaction transaction);
}
