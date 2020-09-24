package com.cs203t5.ryverbank.entity.Transaction;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionServices{
    private TransactionRepository transactions;

    public TransactionServiceImpl(TransactionRepository transactions) {
        this.transactions = transactions;
    }

    @Override
    public List<Transaction> listTransactions() {
        return transactions.findAll();
    }

    @Override 
    public Transaction getTransaction(Long id){
        return transactions.findById(id).map(transaction -> {
            return transaction;
        }).orElse(null);
    }

    @Override
    public Transaction addTransaction(Transaction transaction) {
        return transactions.save(transaction);
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction newTransInfo){
        return transactions.findById(id).map(transaction -> {
            transaction.setValue(newTransInfo.getValue());
            transaction.setTransactionType(newTransInfo.getTransactionType());
            return transactions.save(transaction);
        }).orElse(null);    
    }

    @Override
    public void deleteTransaction(Long id){
        transactions.deleteById(id);
    }
}
