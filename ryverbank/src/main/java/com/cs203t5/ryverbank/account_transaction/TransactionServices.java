package com.cs203t5.ryverbank.account_transaction;

/**
 * An interface for various transaction services.
 */
public interface TransactionServices {
    /**
     * Creates a normal transaction based on the transaction information. If the
     * transferer or receiver account is not found, throw AccountNotFoundException.
     * 
     * @param transaction The transaction information.
     * @return The transaction created.
     */
    Transaction addTransaction(Transaction transaction);

    /**
     * Creates a trade transaction based on the transaction information. This method
     * can only be used when there is a matched trade, otherwise the unmatched trade
     * will be updated in TradeServiceImpl.
     * 
     * @param acc1 The buyer/seller account.
     * @param acc2 The account being traded with acc1.
     * @param amt  The amount traded.
     * @return The trade transaction created.
     */
    Transaction addTransaction(Long acc1, Long acc2, double amt);
}
