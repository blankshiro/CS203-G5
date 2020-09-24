package com.cs203t5.ryverbank.account_transaction;

import java.util.List;

public interface AccountServices {
    List<Account> listAccounts();
    Account getAccount(Long accNumber);
    Account addAccount(Account account);
    Account updateAccount(Long accNumber, Account account);

    void deleteAccount(Long accNumber);
}
