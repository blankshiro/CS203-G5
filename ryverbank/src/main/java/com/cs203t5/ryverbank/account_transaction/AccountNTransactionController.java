package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.util.Optional;

import org.dom4j.rule.NullAction;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
            System.out.println(username + "\n\n\n\n\n");
        } 
        else {
            // username = principal.toString();
        }
        //retrieve optionalCustomer object from repo
        Optional<Customer> optionalCustomer = cusRepo.findByUsername(username);
        //get customer object from optional object
        if(optionalCustomer != null && optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();
            this.sessionID = customer.getId();
        }
        
    }

    /*
    change the Long id to equal to the session stored value
    */
    @GetMapping("/accounts")
    public List<Account> getAllAccounts(){
        //get id
        getSessionDetails();
        Long id = sessionID;
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
        getSessionDetails();
        Long id = sessionID;
        Account newAcc = new Account();

        return cusRepo.findById(id).map(customer -> {
            newAcc.setCustomerId(id);
            newAcc.setBalance(newAccInfo.getCustomerId());
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
        Transaction newTrans = new Transaction();

        return accRepo.findById(accId).map(account -> {
            newTrans.setAmount(newTransInfo.getAmount());
            newTrans.setFrom(account.getId());
            newTrans.setTo(newTransInfo.getTo());
            return transService.addTransaction(newTrans);
        }).orElseThrow(() -> new AccountNotFoundException(accId));
    }
} 
