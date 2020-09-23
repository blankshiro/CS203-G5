package com.cs203t5.ryverbank.entity.Account;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cs203t5.ryverbank.entity.User.*;

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
    public List<Account> getAllAccountsByUserId(@PathVariable (value = "userId") String userId){
        if(!users.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return accounts.findByUserId(userId);
    }

    @PostMapping("/users/{userID}/accounts")
    public Account addAccount(@PathVariable (value = "userId") String userId, @RequestBody Account account){
        return users.findById(userId).map(user -> {
            account.setUser(user);
            account.setAccType(account.getAccType());
            account.setBalance(account.getBalance());
            account.setLimit(account.getLimit());
            return accService.addAccount(account);
        }).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @PutMapping("/users/{userId}/accounts/{accNumber}")
    public Account updateAccount(@PathVariable (value = "userId") String userId, 
                                    @PathVariable (value = "accNumber") Long accNumber,
                                    @RequestBody Account newAccInfo) {
        if(!users.existsById(userId)){
            throw new UserNotFoundException(userId);
        } 
        return accounts.findByaccNumberAndUserID(accNumber, userId).map(account -> {
            account.setAccType(newAccInfo.getAccType());
            account.setBalance(newAccInfo.getBalance());
            account.setLimit(newAccInfo.getLimit());
            return accService.updateAccount(accNumber, account);
        }).orElseThrow(() -> new AccountNotFoundException(accNumber));
    }

    @DeleteMapping("/users/{userID}/deleteAcc/{accNumber}")
    public ResponseEntity<?> deleteAccount(@PathVariable (value = "userId") String userId,
                                            @PathVariable (value = "accNumber") Long accNumber){
        if(users.existsById(userId)){
            throw new UserNotFoundException(userId);
        }
        return accounts.findByaccNumberAndUserID(accNumber, userId).map(account -> {
            accounts.delete(account);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new AccountNotFoundException(accNumber));
    }
    
}
