package com.cs203t5.ryverbank.trading;
import java.util.Date;
import javax.persistence.*;
import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.account_transaction.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Trade {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    private String action;

    @JsonProperty("symbol")
    private String symbol;

    private int quantity;

    private double bid;

    private double ask;

    private double avg_price;

    private int filled_quantity;

    private Date date;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "customerId", referencedColumnName = "id", updatable = false, insertable = false)
    private Customer user;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "accountId", referencedColumnName = "id", updatable = false, insertable = false)
    private Account account;


}
