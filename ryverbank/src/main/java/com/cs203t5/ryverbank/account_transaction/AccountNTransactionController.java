package com.cs203t5.ryverbank.account_transaction;

import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import com.cs203t5.ryverbank.customer.*;

/**
 * A AccountNTransactionController that accepts and returns account and
 * transaction JSON data.
 */
@RestController
public class AccountNTransactionController {
    /** The account repository. */
    private AccountRepository accRepo;
    /** The customer repository. */
    private CustomerRepository cusRepo;
    /** The transaction repository. */
    private TransactionRepository transRepo;
    /** The account services. */
    private AccountServices accService;
    /** The transaction services. */
    private TransactionServices transService;

    private CustomerService cusService;

    private Long sessionID = 1L; // this is to retrieve id from customer retrieve from securitycontextholder

    /**
     * Constructs a AccountNTransactionController with the following parameters.
     * 
     * @param accRepo      THe account repository.
     * @param cusRepo      The customer repository.
     * @param transRepo    The transaction repository.
     * @param accService   The account services.
     * @param transService The transaction services.
     */
    public AccountNTransactionController(AccountRepository accRepo, CustomerRepository cusRepo,
            TransactionRepository transRepo, AccountServices accService, TransactionServices transService, CustomerService cusService) {
        this.accRepo = accRepo;
        this.cusRepo = cusRepo;
        this.transRepo = transRepo;
        this.accService = accService;
        this.transService = transService;
        this.cusService = cusService;
    }

    /**
     * 
     */
    public void getSessionDetails() {
        String username = "";
        // Inside the SecurityContextHolder we store details of the principal currently
        // interacting with the application.
        // Spring Security uses an Authentication object to represent this information.
        // call out securitycontextholder to get session
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal != null && principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername(); // retrieve session userdetails and store into username
        } else {
            // username = principal.toString();
        }
        // retrieve optionalCustomer object from repo
        Optional<Customer> optionalCustomer = cusRepo.findByUsername(username);
        // get customer object from optional object
        if (optionalCustomer != null && optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            this.sessionID = customer.getCustomerId();
        }

    }

    /**
     * Find the list of all the accounts owned by the customer. If the customer is
     * not found, throw CustomerNotFoundException. If there is no account found,
     * throw AccountNotFoundException.
     * 
     * @return The list of accounts found.
     */
    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        // get id
        getSessionDetails();
        Long id = sessionID;

        if (!cusRepo.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }

        // This statement searches based on the customer, instead of the userId
        if (accService.listAccounts(id).isEmpty()) {
            throw new AccountNotFoundException("No account created for this customer.");
        }
        return accService.listAccounts(id);
    }

    /**
     * Finds the account based on the specified account id. If there is no account
     * found, throw AccountNotFoundException. If the the customer is not the owner
     * of the account, throw CustomerUnauthorizedException.
     * 
     * @param id The account id.
     * @return The account found.
     */
    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable Long id) {
        getSessionDetails();
        Long session = sessionID;

        Account acc = accService.getAccount(id);

        if (acc == null) {
            throw new AccountNotFoundException(id);
        }

        if (session != acc.getCustomer_id()) {
            throw new CustomerUnauthorizedException("Account does not belong to this customer");
        }

        return acc;
    }

    /**
     * Creates an account based on the specified account information. If there is no
     * customer found based on the information, throw CustomerNotFoundException.
     * 
     * @param newAccInfo The account information.
     * @return The account created.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts")
    public Account createAccount(@RequestBody Account newAccInfo) {
        return cusRepo.findById(newAccInfo.getCustomer_id()).map(aCustomer -> {
            newAccInfo.setCustomer(aCustomer);
            newAccInfo.setAvailableBalance(newAccInfo.getBalance());
            return accService.addAccount(newAccInfo);

        }).orElseThrow(() -> new AccountCustomerNotFoundException(newAccInfo.getCustomer_id()));
    }

    /**
     * Finds the list of all transaction based on the account id. If there is no
     * account found, throw AccountNotFoundException. If the customer is not the
     * owner of the account, throw CustomerUnauthorizedException.
     * 
     * @param id The account id.
     * @return The list of transactions found
     */
    @GetMapping("/accounts/{id}/transactions")
    public List<Transaction> getAllTransaction(@PathVariable Long id) {
        getSessionDetails();
        Long session = sessionID;

        Account acc = accService.getAccount(id);

        // check if the account is valid
        if (acc == null) {
            throw new AccountNotFoundException(id);
        }

        // check if acc belong to the customer
        if (acc.getCustomer_id() != session) {
            throw new CustomerUnauthorizedException("Account does not belong to this customer");
        }

        return transRepo.findByAccount1OrAccount2(id, id);
    }

    /**
     * Creates a transaction based on the account id and transaction information. If
     * the customer is not the owner of the account, throw
     * CustomerUnauthorizedException. The the account does not exist, throw
     * AccountNotFoundException.
     * 
     * @param id           The account id.
     * @param newTransInfo The transaction information.
     * @return The transaction created.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts/{id}/transactions")
    public Transaction addTransaction(@PathVariable Long id, @RequestBody Transaction newTransInfo, Authentication auth) {
        String username = auth.getName();

        Optional<Customer> user = cusRepo.findByUsername(username);
        Customer cus = null;
        if(user != null){
            cus = user.get();
        }
        
        Long sender = newTransInfo.getAccount1();
        Long receiver = newTransInfo.getAccount2();
        // check if the accounts_id belong to the customer
        Account acc = accService.getAccount(sender);
        if(acc == null){
            throw new AccountNotFoundException("No such account");
        }
        if(acc.getCustomer_id() != cus.getCustomerId()){
            throw new CustomerUnauthorizedException("Account does not belong to this customer");
        }

        // check if the receiver is valid
        if(accService.getAccount(receiver) == null){
            throw new AccountNotFoundException(receiver);
        }
        System.out.println("successful");
        return transService.addTransaction(newTransInfo);
    }

}
