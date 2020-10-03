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
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "customerId", referencedColumnName = "id")
    @JsonProperty("customer_id")
    private Long customer;

    private double balance;

    @JsonProperty("available_balance")
    private double availableBalance;

    // @OneToMany(mappedBy = "account1", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    // private List<Transaction> transactions;

    public Account(double balance, Long customer, double availableBalance){
        // this.transactions = new ArrayList<Transaction>();
        this.balance = balance;
        this.customer = customer;
        this.availableBalance = availableBalance;
    }
}
