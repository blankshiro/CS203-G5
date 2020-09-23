package com.cs203t5.ryverbank.entity.Account;

import java.util.List;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import com.cs203t5.ryverbank.entity.User.*;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Account {
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long accNumber;
    private String accType;
    private double balance;
    private double limit;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Account(long accNumber, String accType, double balance, double limit){
        this.accNumber = accNumber;
        this.accType = accType;
        this.balance = balance;
        this.limit = limit;
    }
}
