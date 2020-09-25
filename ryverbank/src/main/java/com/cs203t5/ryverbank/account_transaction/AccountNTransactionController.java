package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.cs203t5.ryverbank.customer.*;

@RestController
public class AccountNTransactionController {
    private AccountRepository accRepo;
    private CustomerRepository cusRepo;
    private TransactionRepository transRepo;
    private AccountServices accService;
    private TransactionServices transService;

    public AccountNTransactionController(AccountRepository accRepo, CustomerRepository cusRepo, 
                                        TransactionRepository transRepo, AccountServices accService, 
                                        TransactionServices transService){
        this.accRepo = accRepo;
        this.cusRepo = cusRepo;
        this.transRepo = transRepo;
        this.accService = accService;
        this.transService = transService;
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts(Customer cus){
        Long id = cus.getId();
        if(!cusRepo.existsById(id)){
            throw new CustomerNotFoundException(id);
        }
        return accRepo.findByCustomerId(id);
    }

    @GetMapping("/accounts/{accounts_id}")
    public Account getAccount(@PathVariable (value = "account_id") Long accId){
        Account acc = accService.getAccount(accId);

        if(acc == null){
            throw new AccountNotFoundException(accId);
        }
        return accService.getAccount(accId);
    }

    @GetMapping("/accounts/{accounts_id}/transactions")
    public List<Transaction> getAllTransaction(@PathVariable (value = "accId") Long accId){
        if(!accRepo.existsById(accId)){
             throw new AccountNotFoundException(accId);
        }
        return transRepo.findByFromOrTo(accId, accId);
    }


} 
