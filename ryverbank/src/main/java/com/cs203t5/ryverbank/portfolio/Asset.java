package com.cs203t5.ryverbank.portfolio;

import javax.persistence.*;

import com.cs203t5.ryverbank.customer.Customer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

//all the details of asset can be obtain from trade repository

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("avg_price")
    private double avgPrice;

    @JsonProperty("current_price")
    private double currentPrice;

    @JsonProperty("value")
    private double value;

    @JsonProperty("gain_loss")
    private double gainLoss;

    @JsonIgnore
    Long customerId;

    //if not traded should be false
    @JsonIgnore
    String isTraded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "portfolioId", referencedColumnName = "customerId", updatable = false, insertable = false)
    private Portfolio portfolio;

    public Asset(String code, int quantity, double avgPrice, double currentPrice, Long customerId, String isTraded){
        
        this.code = code;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
        this.currentPrice = currentPrice;
        this.customerId = customerId;
        this.isTraded = isTraded;
        this.value = this.currentPrice * quantity;
        this.gainLoss = value - (avgPrice * quantity);
        
    }
    
}
