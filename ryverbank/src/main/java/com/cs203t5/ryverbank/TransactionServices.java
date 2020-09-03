package com.cs203t5.ryverbank;
import java.util.ArrayList;

public interface TransactionServices {

    ArrayList<Transaction> listTransactions();
    Transaction addTransaction (Transaction newTransaction);

}
