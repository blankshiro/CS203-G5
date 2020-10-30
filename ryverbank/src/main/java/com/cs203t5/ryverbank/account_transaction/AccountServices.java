package com.cs203t5.ryverbank.account_transaction;

import java.util.List;

/**
 * An interface for various account services.
 */
public interface AccountServices {
    List<Account> listAccounts(Long cusId);

    Account getAccount(Long accNumber);

    Account addAccount(Account account);

    Account fundTransfer(Long accId, double amt);

    Account accTradeOnHold(Long accId, double amt);

    Account accTradeApproved(Long accId, double amt);
}
