package com.cs203t5.ryverbank.account_transaction;

import java.util.List;

/**
 * An interface for various transaction services.
 */
public interface TransactionServices {
    Transaction addTransaction(Transaction transaction);

    Transaction addTransaction(Long acc1, Long acc2, double amt);
}
