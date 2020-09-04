package com.cs203t5.ryverbank.controller;

import com.cs203t5.ryverbank.service.*;
import com.cs203t5.ryverbank.entity.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

//This class is used to show the options that are available when a client logs in
@RestController
public class TransactionsController {
    private TransactionServices transactionService;

    public TransactionsController (TransactionServices injectedTransactions){
        this.transactionService = injectedTransactions;
    }
    //This line will probably come after myaccount/mytransactions
    @RequestMapping("/mytransactions")
    public List<Transaction> displayTransactions (){
        return transactionService.listTransactions();
    }

}
