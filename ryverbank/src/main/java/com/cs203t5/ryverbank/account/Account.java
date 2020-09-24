package com.cs203t5.ryverbank.account;

import java.util.List;
import javax.persistence.*;

import com.cs203t5.ryverbank.customer.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Account {
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;
    private Long customer_id;
    private double balance;
    private double available_balance;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Account(long id, double balance, double available_balance){
        this.id = id;
        this.customer_id = user.getId();
        this.balance = balance;
        this.available_balance = available_balance;
    }
}
