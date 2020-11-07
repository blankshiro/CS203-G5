package com.cs203t5.ryverbank.account_transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

public class AccountServiceTest {
    
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;


    //testing creating account
    @Test
    public void addAcc_GetAcc_ReturnAcc(){
        
        Account foundAcc = new Account(1L, 5000.0, 1000.0);
        when(accountRepository.save(any(Account.class))).thenReturn(foundAcc);

        Account acc = accountServiceImpl.addAccount(foundAcc);
        assertNotNull(acc);

        verify(accountRepository).save(acc);
    }



    //test getting the account
    @Test
    public void getAcc_FoundAcc_ReturnAcc(){
        Account foundAcc = new Account(1L, 5000.0, 1000.0);
        when(accountRepository.save(any(Account.class))).thenReturn(foundAcc);
        when(accountRepository.findById(foundAcc.getAccountID())).thenReturn(Optional.of(foundAcc));
       
        Account acc = accountServiceImpl.getAccount(foundAcc.getAccountID());

        assertNotNull(acc);

        verify(accountRepository).findById(foundAcc.getAccountID());
    }

    //test successful transfer (deduction)
    @Test
    public void getFundTransfer_ReturnAcc(){
        Account foundAcc = new Account(1L, 5000.0, 1000.0);

        //mock a get and save account
        when(accountRepository.save(any(Account.class))).thenReturn(foundAcc);
        when(accountRepository.findById(foundAcc.getAccountID())).thenReturn(Optional.of(foundAcc));
       
        //if transfer amount not exceeding available balance, deduct it both avail and balance
        double amount = -300.0;

        Account account = accountServiceImpl.fundTransfer(foundAcc.getAccountID(), amount);

        assertEquals(700.0, account.getAvailableBalance());
        assertEquals(4700.0, account.getBalance());
    }


    //test unsuccessful transfer, should throw InsufficientBalanceException
    @Test
    public void getFundTransfer_InsufficientBalance(){
        Account foundAcc = new Account(1L, 5000.0, 1000.0);
        //mock a get account
        when(accountRepository.findById(foundAcc.getAccountID())).thenReturn(Optional.of(foundAcc));

        double amount = -2000.0;

        assertThrows(InsufficientBalanceException.class, ()->accountServiceImpl.fundTransfer(foundAcc.getAccountID(), amount));
    }


    //account trade onhold, return account after changes in available balance
    @Test
    public void tradeOnHold_ReturnAcc(){
        Account account = new Account(1L, 8000.0, 3000.0);

        //mock a get and save account
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountRepository.findById(account.getAccountID())).thenReturn(Optional.of(account));

        double amount = -2500.0;

        Account savedAcc = accountServiceImpl.accTradeOnHold(account.getAccountID(), amount);

        assertEquals(3000.0 - 2500.0, savedAcc.getAvailableBalance());
    }
   
    @Test
    public void tradeApproved_ReturnAcc(){
        Account account = new Account(1L, 8000.0, 3000.0);

        //mock a get and save account
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountRepository.findById(account.getAccountID())).thenReturn(Optional.of(account));

        double amount = -2456.56;

        Account updatedAcc = accountServiceImpl.accTradeApproved(account.getAccountID(), amount);

        assertEquals(8000.0 - 2456.56, updatedAcc.getBalance());
    }

    @Test
    public void tradeOnHold_InsufficientBalance(){
        Account account = new Account(1L, 8000.0, 3000.0);
        when(accountRepository.findById(account.getAccountID())).thenReturn(Optional.of(account));

        double amount = -5580.00;

        assertThrows(InsufficientBalanceException.class, () -> accountServiceImpl.accTradeOnHold(account.getAccountID(), amount));
    }
}
