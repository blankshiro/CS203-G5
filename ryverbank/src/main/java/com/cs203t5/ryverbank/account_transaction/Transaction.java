package com.cs203t5.ryverbank.account_transaction;

import javax.persistence.*;

import com.cs203t5.ryverbank.account_transaction.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

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

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "accfrom", referencedColumnName = "id")
    // @JsonProperty("from")
    // private Account account1;
    private Long account1;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "accTo", referencedColumnName = "id")
    // @JsonProperty("to")
    // private Account account2;
    private Long account2;


    public Transaction(Long id, double amt) {
        this.id = id;
        this.amount = amt;
    }

}
