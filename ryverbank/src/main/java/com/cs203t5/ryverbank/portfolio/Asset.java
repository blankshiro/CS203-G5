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

    //if not traded should be false
    @JsonIgnore
    @Column(name = "istraded")
    boolean isTraded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    // @MapsId("customerId")
    @JoinColumn(name = "portfolioId", referencedColumnName = "id", updatable = false, insertable = false)
    private Portfolio portfolio;

    @JsonIgnore
    Long portfolioId;

    @JsonIgnore
    String record;

    public Asset(String code, int quantity, double avgPrice, double currentPrice, Long portfolioId, boolean isTraded, String record){
        
        this.code = code;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
        this.currentPrice = currentPrice;
        this.portfolioId = portfolioId;
        this.isTraded = isTraded;
        this.record = record;
        this.value = this.currentPrice * quantity;
        this.gainLoss = value - (avgPrice * quantity);
        
    }
    
}
