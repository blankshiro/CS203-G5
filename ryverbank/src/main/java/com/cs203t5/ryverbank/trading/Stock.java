package com.cs203t5.ryverbank.trading;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.*;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Stock {
    // All numbers are double except quantity/volume (int) and timestamp (long).
    private String symbol;

    private double lastPrice;

    private int bidVolume;

    private double bid;

    private int askVolume;

    private double ask;

    public Stock(String symbol, double lastPrice, int bidVolume, double bid, int askVolume, double ask) {
        this.symbol = symbol;
        this.lastPrice = lastPrice;
        this.bidVolume = bidVolume;
        this.bid = bid;
        this.askVolume = askVolume;
        this.ask = ask;
    }
}
