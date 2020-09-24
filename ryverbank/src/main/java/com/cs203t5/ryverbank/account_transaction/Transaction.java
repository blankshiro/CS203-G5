package com.cs203t5.ryverbank.account_transaction;

import javax.persistence.*;

import com.cs203t5.ryverbank.account_transaction.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long from;
    private Long to;

    @JsonIgnore

    // many transaction to one user
    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Account account;

    public Transaction(Long id, double amt, Long from, Long to) {
        this.id = id;
        this.amount = amt;
        this.from = from;
        this.to = to;
    }

}
