package com.cs203t5.ryverbank.trading;

import java.util.Date;
import javax.persistence.*;
import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.account_transaction.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

import lombok.*;

/**
 * Trade class for trade management.
 */
@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @NotNull(message = "Action cannot be null")
    private String action;

    @NotNull(message = "Symbol cannot be null")
    private String symbol;

    @NotNull(message = "Quantity cannot be null")
    @Column(name = "quantity")
    private int quantity = -531;

    @Column(name = "bid")
    @JsonProperty("bid")
    private double bid = -1;

    @Column(name = "ask")
    @JsonProperty("ask")
    private double ask;

    @JsonProperty("avg_price")
    private double avgPrice;

    @JsonProperty("filled_quantity")
    private int filledQuantity;

    private Long date;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("customer_id")
    private Long customerId;

    private String status;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "customerId", referencedColumnName = "customerId", updatable = false, insertable = false)
    private Customer user;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "accountId", referencedColumnName = "accountID", updatable = false, insertable = false)
    private Account account;

    /**
     * Constructs a new trade with the following parameters.
     * 
     * @param action         The action by the customer. (specify "buy" for buying
     *                       and "sell" for selling)
     * @param symbol         The stock symbol.
     * @param quantity       The quantity of stock.
     * @param bid            The bidding price. (specify 0.0 for market order,
     *                       ignored if action is "sell")
     * @param ask            The asking price. (specify 0.0 for market order,
     *                       ignored if action is "buy")
     * @param avgPrice       The average price. (the average filled price, as one
     *                       trade can be matched by several other trades)
     * @param filledQuantity The amount of stock quantity filled.
     * @param date           The date of the trade.
     * @param accountId      The account to debit or to deposit to.
     * @param customerId     The customer performing the trade.
     * @param status         The status of the trade. ("open", "filled",
     *                       "partial-filled", "cancelled" or "expired")
     */
    public Trade(String action, String symbol, int quantity, double bid, double ask, double avgPrice,
            int filledQuantity, Long date, Long accountId, Long customerId, String status) {
        this.action = action;
        this.symbol = symbol;
        this.quantity = quantity;
        this.bid = bid;
        this.ask = ask;
        this.avgPrice = avgPrice;
        this.filledQuantity = filledQuantity;
        this.date = date;
        this.accountId = accountId;
        this.customerId = customerId;
        this.status = status;

    }

}
