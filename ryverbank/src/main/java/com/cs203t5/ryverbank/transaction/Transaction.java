package com.cs203t5.ryverbank.transaction;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import com.cs203t5.ryverbank.user.*;

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
    private String transactionType;

    @JsonIgnore

    // many transaction to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Transaction(long id, double amt, String tType) {
        this.id = id;
        this.amount = amt;
        this.transactionType = tType;
    }

}
