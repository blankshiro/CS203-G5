package com.cs203t5.ryverbank.entity.Transaction;

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
public class Transaction {
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long transactionID;
    private double value;
    private String transactionType;

    @JsonIgnore

    //many transaction to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public Transaction(long transactionID, double value, String tType){
        this.transactionID = transactionID;
        this.value = value;
        this.transactionType = tType;
    }

    
}
