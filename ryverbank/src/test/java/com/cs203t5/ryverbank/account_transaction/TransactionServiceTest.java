package com.cs203t5.ryverbank.account_transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doReturn;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    
    @Mock
    private TransactionRepository transactions;

    @Mock
    private AccountRepository accounts;

    @InjectMocks
    private TransactionServiceImpl tImpl;

    @Mock
    private AccountServiceImpl accService;;


    @Test
    public void getAllTransactions(){
        Transaction transaction1 = new Transaction(2L, 3L, 400.0);
        Transaction transaction2 = new Transaction(2L, 3L, 1400.0);

        List<Transaction> list = new ArrayList<>();

        when(transactions.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));
        list = tImpl.listTransactions();

        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    public void getTransaction_returnTransaction(){
        Transaction transaction = new Transaction(2L, 3L, 1350.0);
        Long id = transaction.getId();

        when(transactions.findById(id)).thenReturn(Optional.of(transaction));

        Transaction getTransaction = tImpl.getTransaction(id);

        assertNotNull(getTransaction);

        verify(transactions).findById(id);
    }

    @Test
    public void getTransaction_returnNull(){
        Long id = 6L;

        when(transactions.findById(id)).thenReturn(Optional.empty());

        Transaction transaction = tImpl.getTransaction(id);
        
        assertNull(transaction);

        verify(transactions).findById(id);
    }

    @Test
    public void createTransaction_returnTransaction(){
        
        Transaction transaction = new Transaction(2L, 3L, 400.0);
        Account acc1 = new Account(5L, 8000.0, 4000.0);
        Account acc2 = new Account(6L, 8000.0, 3000.0);
        
        when(transactions.save(any(Transaction.class))).thenReturn(transaction);
      
        doReturn(acc1,acc2).when(accService).getAccount(any(Long.class));
       
        Transaction saved = tImpl.addTransaction(transaction);

        assertNotNull(saved);

        verify(transactions).save(saved);
    }

    //throw exception
    @Test
    public void createTransaction_throwAccountNotFound(){
        Transaction transaction = new Transaction(2L, 3L, 400.0);
        
        assertThrows(AccountNotFoundException.class, () -> tImpl.addTransaction(transaction));
    }
}
