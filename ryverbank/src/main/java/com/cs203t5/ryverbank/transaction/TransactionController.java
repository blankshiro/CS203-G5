package com.cs203t5.ryverbank.transaction;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.cs203t5.ryverbank.account.*;
import com.cs203t5.ryverbank.user.*;

//This class is used to show the options that are available when a client logs in

@RestController
public class TransactionController {
    private TransactionRepository transactions;
    private AccountRepository accounts;

    public TransactionController (TransactionRepository transactions, AccountRepository accounts){
        this.transactions = transactions;
        this.accounts = accounts;
    }

    @GetMapping("/accounts/{accId}/transactions")
    public List<Transaction> getAllTransactionsByUserId(@PathVariable (value = "userId") Long userId){
        if(!accounts.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return transactions.findByUserId(userId);
    }

    @PostMapping("/accounts/{accId}/transactions")
    public Transaction addTransaction(@PathVariable (value = "userId") Long accId, @RequestBody Transaction transaction){
        return accounts.findById(accId).map(account -> {
            transaction.setAmount(transaction.getAmount());
            transaction.setFrom(account.getCustomer_id());
            transaction.setTo(transaction.getTo());
            return transactions.save(transaction);
        }).orElseThrow(() -> new AccountNotFoundException(accId));
    }

    @PutMapping("/accounts/{accId}/transactions/{transactionId}")
    public Transaction updateTransaction(@PathVariable (value = "accId") Long accId,
                                            @PathVariable (value = "transactionId") Long transactionId,
                                            @RequestBody Transaction newTransInfo) {
        if(!accounts.existsById(accId)){
            throw new UserNotFoundException(accId);
        }
        return transactions.findByidAndUserId(transactionId, accId).map(transaction -> {
            transaction.setAmount(newTransInfo.getAmount());
            transaction.setFrom(newTransInfo.getFrom());
            transaction.setTo(newTransInfo.getTo());
            return transactions.save(transaction);
        }).orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    // @DeleteMapping("/users/{userId}/transactions/{transactionId}")
    // public ResponseEntity<?> deleteTransaction(@PathVariable (value = "userId") Long userId,
    //                                             @PathVariable (value = "transactionId") Long transactionId) {
    //     if(!users.existsById(userId)){
    //         throw new UserNotFoundException(userId);
    //     }

    //     return transactions.findByidAndUserId(transactionId, userId).map(transaction -> {
    //         transactions.delete(transaction);
    //         return ResponseEntity.ok().build();
    //     }).orElseThrow(() -> new TransactionNotFoundException(transactionId));
                        
    // }
}
