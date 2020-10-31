package com.cs203t5.ryverbank.account_transaction;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import com.cs203t5.ryverbank.customer.*;

/**
 * Account class for for fund transfers and trades. Each customer can have many
 * accounts.
 */
@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long accountID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Customer customer;

    private Long customer_id;
    private double balance;

    @JsonProperty("available_balance")
    private double availableBalance;

    /**
     * Constructs a new accounts with the following parameters.
     * 
     * @param customer_id      The id of the customer.
     * @param balance          The account balance.
     * @param availableBalance The available balance (fund can be on-hold due to
     *                         pending buy trades)
     */
    public Account(Long customer_id, double balance, double availableBalance) {
        this.customer_id = customer_id;
        this.balance = balance;
        this.availableBalance = availableBalance;
    }
}
