package com.cs203t5.ryverbank.portfolio;

import java.util.List;

import javax.persistence.*;

import javax.validation.constraints.NotNull;

import com.cs203t5.ryverbank.customer.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Portfolio {
    
    @OneToOne(fetch = FetchType.LAZY)
    private Customer customer;


    @Id
    @NotNull(message = "Must have customer id")
    @JsonProperty("customer_id")
    private Long customerId;


    private List<Asset> assets;

    private double unrealized_gain_loss;
    private double total_gain_loss;

    public Portfolio(Long id){
        this.customerId = id;
    }

    
}
