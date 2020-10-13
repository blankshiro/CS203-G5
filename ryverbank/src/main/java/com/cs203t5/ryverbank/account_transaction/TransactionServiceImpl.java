package com.cs203t5.ryverbank.account_transaction;

import java.util.List;


import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionServices {

    private TransactionRepository transactions;
    private AccountServices accService;

    public TransactionServiceImpl(TransactionRepository transactions, AccountServices accService) {
        this.transactions = transactions;
        this.accService = accService;
    }

    @Override
    public List<Transaction> listTransactions() {
        return transactions.findAll();
    }

    @Override
    public Transaction getTransaction(Long id) {
        return transactions.findById(id).map(transaction -> {
            return transaction;
        }).orElse(null);
    }

    @Override
    public Transaction addTransaction(Transaction transaction) {
        Long acc1 = transaction.getAccount1();
        Long acc2 = transaction.getAccount2();
        if(accService.getAccount(acc1) != null){
            accService.fundTransfer(acc1, transaction.getAmount()*-1);
        }
        if(accService.getAccount(acc2) != null){
            accService.fundTransfer(acc2, transaction.getAmount());
        }

        return transactions.save(transaction);
    }
}
