package com.cs203t5.ryverbank.account_transaction;

import java.util.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.cs203t5.ryverbank.customer.*;

@RestController
public class AccountNTransactionController {
    private AccountRepository accRepo;
    private CustomerRepository cusRepo;
    private TransactionRepository transRepo;
    private AccountServices accService;
    private TransactionServices transService;

    private Long sessionID = 1L; //this is to retrieve id from customer retreive from securitycontextholder
    private Customer cusLogged;

    public AccountNTransactionController(AccountRepository accRepo, CustomerRepository cusRepo, 
                                        TransactionRepository transRepo, AccountServices accService, 
                                        TransactionServices transService){
        this.accRepo = accRepo;
        this.cusRepo = cusRepo;
        this.transRepo = transRepo;
        this.accService = accService;
        this.transService = transService;
    }

    
    public void getSessionDetails(){
        String username = "";
        //Inside the SecurityContextHolder we store details of the principal currently interacting with the application. 
        //Spring Security uses an Authentication object to represent this information.
        //call out securitycontextholder to get session
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       
        if (principal != null && principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername(); //retrieve session userdetails and store into username
        } 
        else {
            // username = principal.toString();
        }
        //retrieve optionalCustomer object from repo
        Optional<Customer> optionalCustomer = cusRepo.findByUsername(username);
        //get customer object from optional object
        if(optionalCustomer != null && optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();
            this.sessionID = customer.getCustomerId();
        }
        
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts(){
        //get id
        getSessionDetails();
        Long id = sessionID;
        System.out.println("USER ID IS FOUND: " + sessionID + "\n\n\n\n\n");
        if(!cusRepo.existsById(id)){
            throw new CustomerNotFoundException(id);
        }
        // This statement searches based on the customer, instead of the userId
        return accRepo.findByCustomer(cusRepo.findById(id));
        // return accRepo.findByCustomerCustomerId(id);
    }

    @GetMapping("/accounts/{accounts_id}")
    public Account getAccount(@PathVariable (value = "account_id") Long accId){
        Account acc = accService.getAccount(accId);

        if(acc == null){
            throw new AccountNotFoundException(accId);
        }
        return accService.getAccount(accId);
    }
    

    @PostMapping("/accounts")
    public Account createAccount(@RequestBody Account newAccInfo){
        // if(cusRepo.existsById(newAccInfo.getCustId())){
        //     newAccInfo.setCustomer(cusRepo.getOne(newAccInfo.getCustId()));
        //     return accService.addAccount(newAccInfo);
        // } else {
        //     throw new  CustomerNotFoundException(newAccInfo.getCustId());
        // }
        return cusRepo.findById(newAccInfo.getCustomer_id()).map(aCustomer -> {
            newAccInfo.setCustomer(aCustomer);
            return accService.addAccount(newAccInfo);

        }).orElseThrow(() -> new CustomerNotFoundException(newAccInfo.getCustomer_id()));
        // return cusRepo.findById(id).map(customer -> {
            // newAccInfo.setCustomer(newAccInfo.getId());
            // newAccInfo.setTransactions(null);
        // }).orElseThrow(() -> new CustomerNotFoundException(newAccInfo.getId()));
    }

    @GetMapping("/accounts/{accounts_id}/transactions")
    public List<Transaction> getAllTransaction(@PathVariable (value = "accounts_id") Long accId){
        if(!accRepo.existsById(accId)){
             throw new AccountNotFoundException(accId);
        }
        return transRepo.findByAccount1OrAccount2(accId, accId);
    }

    @PostMapping("/accounts/{accounts_id}")
    public Transaction addTransaction(@PathVariable (value = "accounts_id") Long accId,
                                        @RequestBody Transaction newTransInfo){

        return accRepo.findById(accId).map(account -> {
            return transService.addTransaction(newTransInfo);
        }).orElseThrow(() -> new AccountNotFoundException(accId));
    }
} 
