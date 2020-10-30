package com.cs203t5.ryverbank.account_transaction;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import com.cs203t5.ryverbank.customer.*;

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

    /**
     * Many accounts can be given to one customer, hence @ManyToOne
     * 
     * @JoinColumn(name = The column name you want to have on your table)
     * 
     */

    @ManyToOne(fetch = FetchType.LAZY)
    // By this logic, we can find accounts based on the customer
    // JsonIgnore is important so that we don't see the Customer information
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
