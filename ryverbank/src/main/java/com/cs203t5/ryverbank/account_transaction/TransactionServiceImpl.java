package com.cs203t5.ryverbank.account_transaction;

import org.springframework.stereotype.Service;

/**
 * Implementation of the TransactionServices class.
 * 
 * @see TransactionServices
 */
@Service
public class TransactionServiceImpl implements TransactionServices {
    /** The transaction repository. */
    private TransactionRepository transactions;
    /** The account services. */
    private AccountServices accService;

    /**
     * Constructs a TransactionServiceImpl with the following parameters.
     * 
     * @param transactions The transaction repository.
     * @param accService   The account services.
     */
    public TransactionServiceImpl(TransactionRepository transactions, AccountServices accService) {
        this.transactions = transactions;
        this.accService = accService;
    }

    @Override
    public Transaction addTransaction(Transaction transaction) {
        Long acc1 = transaction.getAccount1();
        Long acc2 = transaction.getAccount2();
        if (accService.getAccount(acc1) != null) {
            accService.fundTransfer(acc1, transaction.getAmount() * -1);
        } else if (accService.getAccount(acc1).getAvailableBalance() < transaction.getAmount()) {
            throw new InsufficientBalanceException("Not enough funds");
        } else {
            throw new AccountNotFoundException(acc1);
        }
        if (accService.getAccount(acc2) != null) {
            accService.fundTransfer(acc2, transaction.getAmount());
        } else {
            throw new AccountNotFoundException(acc2);
        }
        return transactions.save(transaction);
    }

    @Override
    public Transaction addTransaction(Long acc1, Long acc2, double amt) {
        long give, take;
        double total = 0.0;
        if (amt < 0.0) {
            give = acc1;
            take = acc2;
            // buyer balance will reduce
            accService.accTradeApproved(give, amt);
            // seller balance will increase
            accService.accTradeApproved(take, Math.abs(amt));
            total = Math.abs(amt);
        } else {
            give = acc2;
            take = acc1;
            accService.accTradeApproved(give, amt * -1);
            accService.accTradeApproved(take, amt);
            total = amt;
        }
        Transaction transaction = new Transaction(give, take, total);
        return transactions.save(transaction);
    }
}
