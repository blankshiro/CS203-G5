package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.lang.Math;

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
            return accounts.save(account);
        }).orElse(null);
    }

    @Override
    public Account addAccount(Account account){
        return accounts.save(account);
    }

    @Override
    public Account fundTransfer(Long accId, double amt){
        return accounts.findById(accId).map(account -> {
            double curr = account.getAvailableBalance();
            double balance = account.getBalance();
            if(amt < 0.0){
                if(curr - Math.abs(amt) < 0){
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
    
    //use this for pending transaction
    @Override
    public Account accTradeOnHold(Long accId, double amt){
        return accounts.findById(accId).map(account -> {
            double curr = account.getAvailableBalance();
            //so if the amount should be deduct then amt will need to multiply by -1
            //For example,
            //acc1 gives money to acc2
            //then when u use this method 
            //acc1 should do accTradeOnHold(acc1, amt*-1)
            //acc2 should do accTradeOnHold(acc2, amt)
            if(amt < 0.0){
                if(curr - Math.abs(amt) < 0){
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

    //use this for approved transaction
    //always use accTradeOnHold first before this
    //the logic for this method same as the above one
    @Override
    public Account accTradeApproved(Long accId, double amt){
        return accounts.findById(accId).map(account -> {
            double balance = account.getBalance();
            if(amt < 0.0){
                account.setBalance(balance - Math.abs(amt));
            } else {
                account.setBalance(balance + amt);
            }
            return accounts.save(account);
        }).orElse(null);
    }
}
