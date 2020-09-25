package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;
    private Long customerId;
    private double balance;
    private double available_balance;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "customerId", referencedColumnName = "id", updatable = false, insertable = false)
    private Customer user;

    @OneToMany(mappedBy = "from", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    public Account(long id, double balance, double available_balance){
        this.id = id;
        this.customerId = user.getId();
        this.balance = balance;
        this.available_balance = available_balance;
    }
}
