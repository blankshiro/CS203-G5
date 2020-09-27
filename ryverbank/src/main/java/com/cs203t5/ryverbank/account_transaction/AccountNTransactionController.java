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

    /*
    change the Long id to equal to the session stored value
    */
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

    /*
    change the Long id to equal to the session stored value
    */
    @PostMapping("/accounts")
    public Account createAccount(Customer cus, @RequestBody Account newAccInfo){
        Long id = cus.getId();
        Account newAcc = new Account();

        return cusRepo.findById(id).map(customer -> {
            newAcc.setCustomer(cus);
            newAcc.setTransactions(null);
            newAcc.setBalance(newAccInfo.getBalance());
            newAcc.setAvailableBalance(newAccInfo.getAvailableBalance());
            return accService.addAccount(newAcc);
        }).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @GetMapping("/accounts/{accounts_id}/transactions")
    public List<Transaction> getAllTransaction(@PathVariable (value = "accounts_id") Long accId){
        if(!accRepo.existsById(accId)){
             throw new AccountNotFoundException(accId);
        }
        return transRepo.findByFromOrTo(accId, accId);
    }

    @PostMapping("/accounts/{accounts_id}")
    public Transaction addTransaction(@PathVariable (value = "accounts_id") Long accId,
                                        @RequestBody Transaction newTransInfo){
        Transaction newTrans = new Transaction(

        return accRepo.findById(accId).map(account -> {
            newTrans.setAmount(newTransInfo.getAmount());
            newTrans.setAccount1(account);
            newTrans.setAccount2(newTransInfo.getAccount2());
            return transService.addTransaction(newTrans);
        }).orElseThrow(() -> new AccountNotFoundException(accId));
    }
} 
