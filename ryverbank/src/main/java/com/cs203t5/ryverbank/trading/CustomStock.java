package com.cs203t5.ryverbank.trading;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.*;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class CustomStock {

    @Id
    @NotNull(message = "Symbol cannot be null")
    private String symbol;

    @NotNull(message = "last price cannot be null")
    @JsonProperty("last_price")
    private double lastPrice;

    @NotNull(message = "bid volume cannot be null")
    @JsonProperty("bid_volume")
    private int bidVolume;

    @NotNull(message = "bid price cannot be null")
    private double bid;

    @NotNull(message = "ask volume cannot be null")
    @JsonProperty("ask_volume")
    private int askVolume;

    @NotNull(message = "ask price cannot be null")
    private double ask;

    /**
     * Constructs a stock object with the following parameters.
     * 
     * @param symbol    The symbol of the stock.
     * @param lastPrice The last price of the stock.
     * @param bidVolume The bid volume of the stock.
     * @param bid       The bid price of the stock.
     * @param askVolume The ask volume of the stock.
     * @param ask       The ask price of the stock.
     */
    public CustomStock(String symbol, double lastPrice, int bidVolume, double bid, int askVolume, double ask) {
        this.symbol = symbol;
        this.lastPrice = lastPrice;
        this.bidVolume = bidVolume;
        this.bid = bid;
        this.askVolume = askVolume;
        this.ask = ask;
    }
}
