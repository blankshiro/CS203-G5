package com.cs203t5.ryverbank.account_transaction;

import java.util.List;

public interface AccountServices {
    List<Account> listAccounts();
    Account getAccount(Long accNumber);
    Account addAccount(Account account);
    Account fundTransfer(Long accId, double amt);
    Account accTradeOnHold(Long accId, double amt);
    Account accTradeApproved(Long accId, double amt);
}
