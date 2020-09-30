package com.cs203t5.ryverbank.trading;

import java.util.Date;

import javax.persistence.*;

import lombok.*;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Order {
    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    private String action;

    private String symbol;

    private int quantity;

    private double bid;

    private double ask;

    private double avgPrice;

    private int filled_quantity;

    private Date date;

    private Long accountId;

    private Long customerId;

    private String status;

}
