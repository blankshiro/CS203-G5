package com.cs203t5.ryverbank.account_transaction;

import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
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

        if(!cusRepo.existsById(id)){
            throw new CustomerNotFoundException(id);
        }

        // This statement searches based on the customer, instead of the userId
        if(accService.listAccounts(id).isEmpty()){
            throw new AccountNotFoundException("No account created for this customer.");
        }
        return accService.listAccounts(id);
        // return accRepo.findByCustomerCustomerId(id);
    }
    
    // put as id instead of account_id for now, need to check with prof again
    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable Long id){
        getSessionDetails();
        Long session = sessionID;

        Account acc = accService.getAccount(id);

        if(acc == null){
            throw new AccountNotFoundException(id);
        }
        
        if(session != acc.getCustomer_id()){
            throw new CustomerUnauthorizedException("Account does not belong to this customer");
        }

        return acc;
    }
    
    @ResponseStatus(HttpStatus.CREATED)
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
            newAccInfo.setAvailableBalance(newAccInfo.getBalance());
            return accService.addAccount(newAccInfo);

        }).orElseThrow(() -> new CustomerNotFoundException(newAccInfo.getCustomer_id()));
        // return cusRepo.findById(id).map(customer -> {
            // newAccInfo.setCustomer(newAccInfo.getId());
            // newAccInfo.setTransactions(null);
        // }).orElseThrow(() -> new CustomerNotFoundException(newAccInfo.getId()));
    }

    @GetMapping("/accounts/{id}/transactions")
    public List<Transaction> getAllTransaction(@PathVariable Long id){
        getSessionDetails();
        Long session = sessionID;

        Account acc = accService.getAccount(id);

        //check if the account is valid
        if(acc == null){
            throw new AccountNotFoundException(id);
        }

        //check if acc belong to the customer
        if(acc.getCustomer_id() != session){
            throw new CustomerUnauthorizedException("Account does not belong to this customer");
        }

        return transRepo.findByAccount1OrAccount2(id, id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts/{id}/transactions")
    public Transaction addTransaction(@PathVariable Long id,
                                        @RequestBody Transaction newTransInfo){
        getSessionDetails();
        Long session = sessionID;
        Long sender = newTransInfo.getAccount1();
        Long receiver = newTransInfo.getAccount2();
        return accRepo.findById(id).map(account -> {
            //check if the accounts_id belong to the customer
            if(account.getCustomer_id() != session){
                throw new CustomerUnauthorizedException("Account does not belong to this customer");
            }
            //check if the transaction serder account belong to the customer
            if(sender != id){
                throw new CustomerUnauthorizedException("Account does not belong to this customer");
            }
            //check if sender and receiver are the same
            // if(sender == receiver){
            //     throw new InvalidEntryException("Cannot transfer to same account");
            // }

            //check if the receiver is valid
            if(!accRepo.existsById(receiver)){
                throw new AccountNotFoundException(receiver);
            }
            return transService.addTransaction(newTransInfo);
        }).orElseThrow(() -> new AccountNotFoundException(id));
    }

} 
