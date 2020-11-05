package com.cs203t5.ryverbank.account_transaction;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

/**
 * Transaction class for transaction management.
 */
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Transaction {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    private double amount;

    @JsonProperty("from")
    private Long account1;

    @JsonProperty("to")
    private Long account2;

    /**
     * Constructs a transaction with the following parameters.
     * 
     * @param account1 The transferer account.
     * @param account2 The receiver account.
     * @param amt      The amount to be transferred.
     */
    public Transaction(Long account1, Long account2, double amt) {
        this.account1 = account1;
        this.account2 = account2;
        this.amount = amt;
    }

}
