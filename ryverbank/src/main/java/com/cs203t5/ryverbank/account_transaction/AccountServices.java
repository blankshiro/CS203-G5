package com.cs203t5.ryverbank.account_transaction;

import java.util.List;

/**
 * An interface for various account services.
 */
public interface AccountServices {

    /**
     * Find the list of accounts that belong to the specified customer id.
     * 
     * @param cusId The customer id.
     * @return The list of accounts based on the customer id.
     */
    List<Account> listAccounts(Long cusId);

    /**
     * Finds the account that has the specified account id.
     * 
     * @param accNumber The account id.
     * @return The account found.
     */
    Account getAccount(Long accNumber);

    /**
     * Adds an account based on the specified account information.
     * 
     * @param account The account to be added.
     * @return The account added.
     */
    Account addAccount(Account account);

    /**
     * Simulates a fund transfer with the specified account id and amount. If the
     * specified amount is negative, then the account belongs to the transferer. If
     * the specified amount is positive, then the account belongs to the receiver.
     * If the transferer has insufficient balance in the account, throw
     * InsufficientBalanceException.
     * 
     * @param accId The account id.
     * @param amt   The amount to transfer.
     * @return The account that made the transfer.
     */
    Account fundTransfer(Long accId, double amt);

    /**
     * Updates the available account balance when the trade is open with the
     * specified account id and amount. If the specified amount is negative, then
     * the account belongs to the buyer. If the amount is positive, then the
     * specified account belongs to the seller. If the buy has insufficient
     * available balance, throw InsufficientBalanceException.
     * 
     * @param accId The account id.
     * @param amt   The amount to update.
     * @return The account with the updated available balance.
     */
    Account accTradeOnHold(Long accId, double amt);

    /**
     * Updates the account balance when the trade is open with the specified account
     * id and amount. If the specified amount is negative, then the account belongs
     * to the buyer. If the amount is positive, then the specified account belongs
     * to the seller.
     * 
     * @param accId The account id.
     * @param amt   The amount to update.
     * @return The account with the updated balance.
     */
    Account accTradeApproved(Long accId, double amt);
}
