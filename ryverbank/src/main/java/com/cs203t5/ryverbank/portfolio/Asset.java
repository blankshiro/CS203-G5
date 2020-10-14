package com.cs203t5.ryverbank.portfolio;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Asset {
    
    @Id
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Portfolio portfolio;

    public Asset(String code, int quantity, double avgPrice, double currentPrice){
        this.code = code;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
        this.currentPrice = currentPrice;

        this.value = this.currentPrice * quantity;
        this.gainLoss = value - (avgPrice * quantity);
    }
    
}
