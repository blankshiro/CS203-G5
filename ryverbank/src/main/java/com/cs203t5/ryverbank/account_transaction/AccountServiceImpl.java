package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.lang.Math;

import org.springframework.stereotype.Service;

/**
 * Implementation of the AccountServices class.
 * 
 * @see AccountServices
 */
@Service
public class AccountServiceImpl implements AccountServices {
    /** The account repository. */
    private AccountRepository accounts;

    /**
     * Constructs a AccountServiceImpl instance.
     * 
     * @param accounts The account repository.
     */
    public AccountServiceImpl(AccountRepository accounts) {
        this.accounts = accounts;
    }

    /**
     * Find the list of accounts that belong to the specified customer id.
     * 
     * @param cusId The customer id.
     * @return The list of accounts based on the customer id.
     */
    @Override
    public List<Account> listAccounts(Long cusId) {
        return accounts.findAllByCustomerCustomerId(cusId);
    }

    /**
     * Finds the account that has the specified account id.
     * 
     * @param accNumber The account id.
     * @return The account found.
     */
    @Override
    public Account getAccount(Long accNumber) {
        return accounts.findById(accNumber).map(account -> {
            return accounts.save(account);
        }).orElse(null);
    }

    /**
     * Adds an account based on the specified account information.
     * 
     * @param account The account to be added.
     * @return The account added.
     */
    @Override
    public Account addAccount(Account account) {
        return accounts.save(account);
    }

    /**
     * 
     * 
     * @param accId The account id.
     * @param amt The amount to transfer.
     * @return The account that made the transfer.
     */
    @Override
    public Account fundTransfer(Long accId, double amt) {
        return accounts.findById(accId).map(account -> {
            double curr = account.getAvailableBalance();
            double balance = account.getBalance();
            if (amt < 0.0) {
                if (curr - Math.abs(amt) < 0) {
                    throw new InsufficientBalanceException("Not enough funds in account");
                } else {
                    account.setAvailableBalance(curr - Math.abs(amt));
                    account.setBalance(balance - Math.abs(amt));
                }
            } else {
                account.setAvailableBalance(curr + amt);
                account.setBalance(balance + amt);
            }
            return accounts.save(account);
        }).orElse(null);
    }

    /**
     * 
     * @param accId
     * @param amt 
     * @return
     */
    @Override
    public Account accTradeOnHold(Long accId, double amt) {
        return accounts.findById(accId).map(account -> {
            double curr = account.getAvailableBalance();
            if (amt < 0.0) {
                if (curr - Math.abs(amt) < 0) {
                    throw new InsufficientBalanceException("Not enough funds in trade");
                } else {
                    account.setAvailableBalance(curr - Math.abs(amt));
                }
            } else {
                account.setAvailableBalance(curr + amt);
            }
            return accounts.save(account);
        }).orElse(null);
    }

    /**
     * 
     * @param accId
     * @param amt
     * @return
     */
    @Override
    public Account accTradeApproved(Long accId, double amt) {
        return accounts.findById(accId).map(account -> {
            double balance = account.getBalance();
            if (amt < 0.0) {
                account.setBalance(balance - Math.abs(amt));
            } else {
                account.setBalance(balance + amt);
            }
            return accounts.save(account);
        }).orElse(null);
    }
}
