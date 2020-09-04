package com.cs203t5.ryverbank.service;

import com.cs203t5.ryverbank.entity.*;
import java.util.ArrayList;

public interface TransactionServices {

    ArrayList<Transaction> listTransactions();
    Transaction addTransaction (Transaction newTransaction);

}
