package com.cs203t5.ryverbank;

import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class TransactionServicesImpl implements TransactionServices{
    //Create empty arraylist
    private ArrayList<Transaction> allTransactions = new ArrayList<>();

    /*
        Initializing a mock-dataset of transactions 
        We could potentially do injection here to find a user's transactions
    */
    public TransactionServicesImpl() {
        allTransactions.add(new Transaction(1, 100.00, "Transfer"));
        allTransactions.add(new Transaction(2, 200.00, "Deposit"));
        allTransactions.add(new Transaction(3, 10000.00, "Deposit"));
        allTransactions.add(new Transaction(4, 10000000.00, "Robbery"));
    }

    @Override
    public ArrayList<Transaction> listTransactions() {
        return allTransactions;
    }
    @Override
    public Transaction addTransaction(Transaction newTransaction) {
        allTransactions.add(newTransaction);
        return newTransaction;
    }

}
