package com.cs203t5.ryverbank.account;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cs203t5.ryverbank.user.*;

@RestController
public class AccountController {
    private AccountRepository accounts;
    private AccountServices accService;
    private UserRepository users;

    public AccountController(AccountRepository accounts, UserRepository users){
        this.accounts = accounts;
        this.users = users;
    }

    @GetMapping("/users/{userId}/accounts")
    public List<Account> getAllAccountsByUserId(@PathVariable (value = "userId") Long userId){
        if(!users.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return accounts.findByUserId(userId);
    }

    @PostMapping("/users/{userID}/accounts")
    public Account addAccount(@PathVariable (value = "userId") Long userId, @RequestBody Account account){
        return users.findById(userId).map(user -> {
            account.setCustomer_id(user.getId());
            account.setBalance(account.getBalance());
            account.setAvailable_balance(account.getAvailable_balance());
            return accService.addAccount(account);
        }).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @PutMapping("/users/{userId}/accounts/{accId}")
    public Account updateAccount(@PathVariable (value = "userId") Long userId, 
                                    @PathVariable (value = "accId") Long accId,
                                    @RequestBody Account newAccInfo) {
        if(!users.existsById(userId)){
            throw new UserNotFoundException(userId);
        } 
        return accounts.findByaccIdAndUserID(accId, userId).map(account -> {
            account.setBalance(account.getBalance());
            account.setAvailable_balance(account.getAvailable_balance());
            return accService.updateAccount(accId, account);
        }).orElseThrow(() -> new AccountNotFoundException(accId));
    }

    @DeleteMapping("/users/{userID}/deleteAcc/{accNumber}")
    public ResponseEntity<?> deleteAccount(@PathVariable (value = "userId") Long userId,
                                            @PathVariable (value = "accNumber") Long accNumber){
        if(users.existsById(userId)){
            throw new UserNotFoundException(userId);
        }
        return accounts.findByaccIdAndUserID(accNumber, userId).map(account -> {
            accounts.delete(account);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new AccountNotFoundException(accNumber));
    }
    
}
