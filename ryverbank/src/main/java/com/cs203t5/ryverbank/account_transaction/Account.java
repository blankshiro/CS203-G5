package com.cs203t5.ryverbank.account_transaction;

import java.util.*;
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

    /*
        Many accounts can be given to one customer, hence @ManyToOne
        @JoinColumn(name = The column name you want to have on your table)

    */

    @ManyToOne(fetch = FetchType.LAZY)
    // //This statement says that my customer is the foreign key that cannot be null
    // @JoinColumn(name = "customer_fk", nullable = false)
    //By this logic, we can find accounts based on the customer 
    //JsonIgnore is important so that we don't see the Customer information
    @JsonIgnore
    private Customer customer;

    private Long customer_id;
    private double balance;

    @JsonProperty("available_balance")
    private double availableBalance;

    // @OneToMany(mappedBy = "account1", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    // private List<Transaction> transactions;

    public Account(Long customer_id, double balance, double availableBalance){
        // this.transactions = new ArrayList<Transaction>();
        this.customer_id = customer_id;
        this.balance = balance;
        this.availableBalance = availableBalance;
    }
}
