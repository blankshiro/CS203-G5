package com.cs203t5.ryverbank.portfolio;

import java.util.*;

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
    
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    private Customer customer;


    @Id
    // @NotNull(message = "Must have customer id")
    @JsonProperty("customer_id")
    private Long customerId;

    // @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @ElementCollection
    private List<Asset> assets;

    @JsonProperty("unrealized_gain_loss")
    private double unrealizedGainLoss;

    @JsonProperty("total_gain_loss")
    private double totalGainLoss;

    public Portfolio(Long id){
        this.customerId = id;
        this.assets = new ArrayList<>();
    }

    public void setGainLoss(){

        double unrealized_gain_loss = 0; 
        if (!assets.isEmpty()){
            for(Asset asset : assets){
                unrealized_gain_loss += asset.getGainLoss();
            }
        }

        this.unrealizedGainLoss = unrealized_gain_loss;
        this.totalGainLoss += unrealized_gain_loss;
    }

    
}
