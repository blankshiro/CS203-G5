package com.cs203t5.ryverbank.trading;

import javax.persistence.*;

import lombok.*;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class CustomStock {
   
    @Id
    private String symbol;

    private double lastPrice;

    private int bidVolume;

    private double bid;

    private int askVolume;

    private double ask;

    public CustomStock(String symbol, double lastPrice, int bidVolume, double bid, int askVolume, double ask) {
        this.symbol = symbol;
        this.lastPrice = lastPrice;
        this.bidVolume = bidVolume;
        this.bid = bid;
        this.askVolume = askVolume;
        this.ask = ask;
    }
}
