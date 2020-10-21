package com.cs203t5.ryverbank.trading;
import java.util.Date;
import javax.persistence.*;
import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.account_transaction.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

import lombok.*;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Trade {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @NotNull(message = "Action cannot be null")
    private String action;

    @NotNull(message = "Symbol cannot be null")
    private String symbol;

    @NotNull(message = "Quantity cannot be null")
    private int quantity;


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

    public Trade(String action, String symbol, int quantity, double bid, double ask, double avgPrice, int filledQuantity, 
    Long date, Long accountId, Long customerId, String status) {
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
