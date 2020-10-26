package com.cs203t5.ryverbank.account_transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

<<<<<<< Updated upstream
@ResponseStatus(HttpStatus.BAD_REQUEST)
=======
/**
 * InsufficientBalanceException will be thrown when there is insufficient balance in the account.
 * 
 * @see RuntimeException
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
>>>>>>> Stashed changes
public class InsufficientBalanceException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InsufficientBalanceException with the specified detail message.
     * 
     * @param message The message to be printed
     */
    public InsufficientBalanceException(String message){
        super("Error: " + message);
    }
}
