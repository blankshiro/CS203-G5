package com.cs203t5.ryverbank.entity.Account;

import java.util.List;

import javax.transaction.Transaction;

import com.cs203t5.ryverbank.entity.Transaction.TransactionServices;

import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountServices {
    private AccountRepository accounts;

    public AccountServiceImpl(AccountRepository accounts){
        this.accounts = accounts;
    }

    @Override
    public List<Account> listAccounts(){
        return accounts.findAll();
    }

    @Override
    public Account getAccount(Long accNumber){
        return accounts.findById(accNumber).map(account ->{
            return account;
        }).orElse(null);
    }

    @Override
    public Account addAccount(Account account){
        return accounts.save(account);
    }

    @Override
    public Account updateAccount(Long accNumber, Account newAccInfo){
        return accounts.findById(accNumber).map(account -> {
            account.setAccType(newAccInfo.getAccType());
            account.setBalance(newAccInfo.getBalance());
            account.setLimit(newAccInfo.getLimit());
            return accounts.save(account);
        }).orElse(null);
    }

    @Override
    public void deleteAccount(Long accNumber){
        accounts.deleteById(accNumber);
    }

}
