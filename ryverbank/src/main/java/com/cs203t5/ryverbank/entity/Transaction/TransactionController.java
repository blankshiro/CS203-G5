package com.cs203t5.ryverbank.entity.Transaction;

import java.util.List;

import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cs203t5.ryverbank.entity.User.*;
import com.cs203t5.ryverbank.entity.Stock.*;

//This class is used to show the options that are available when a client logs in
@RestController
public class TransactionController {
    private TransactionRepository transactions;
    private UserRepository users;

    public TransactionController (TransactionRepository transactions, UserRepository users){
        this.transactions = transactions;
        this.users = users;
    }


    @GetMapping("/users/{userId}/transactions")
    public List<Transaction> getAllTransactionsByUserId(@PathVariable (value = "userId") String userId){
        if(!users.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return transactions.findByUserId(userId);
    }

    @PostMapping("/users/{userID}/transactions")
    public Transaction addTransaction(@PathVariable (value = "userId") String userId, @RequestBody Transaction transaction){
        return users.findById(userId).map(user -> {
            transaction.setUser(user);
            return transactions.save(transaction);
        }).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @PutMapping("/users/{userId}/transactions/{transactionId}")
    public Transaction updateTransaction(@PathVariable (value = "userId") String userId,
                                            @PathVariable (value = "transactionId") Long transactionId,
                                            @RequestBody Transaction newTransaction) {
        if(!users.existsById(userId)){
            throw new UserNotFoundException(userId);
        }
        return transactions.findByIdAndUserId(transactionId, userId).map(transaction -> {
            transaction.setTransaction(new Transaction(newTransaction.getTransactionID(), newTransaction.getAmount(), newTransaction.getTransactionType()));
            return transactions.save(transaction);
        }).orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    @DeleteMapping("/users/{userID}/transactions/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable (value = "userId") String userId,
                                                @PathVariable (value = "transactionId") Long transactionId) {
        if(!users.existsById(userId)){
            throw new UserNotFoundException(userId);
        }

        return transactions.findByIdAndUserId(transactionId, userId).map(transaction -> {
            transactions.delete(transaction);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new TransactionNotFoundException(transactionId));
                        
    }
}
