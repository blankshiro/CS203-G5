package com.cs203t5.ryverbank.portfolio;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

/**
 * Asset class for asset management.
 */
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

    // if not traded should be false
    @JsonIgnore
    @Column(name = "istraded")
    boolean isTraded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "portfolioId", referencedColumnName = "id", updatable = false, insertable = false)
    private Portfolio portfolio;

    @JsonIgnore
    Long portfolioId;

    @JsonIgnore
    String record;

    /**
     * Constructs an asset with the following parameters.
     * 
     * @param code         The code of the asset.
     * @param quantity     The quantity of asset.
     * @param avgPrice     The average price of asset.
     * @param currentPrice The current price of asset.
     * @param portfolioId  The portfolio id.
     * @param isTraded     Checks whether the asset is traded.
     * @param record       The current records of the asset.
     */
    public Asset(String code, int quantity, double avgPrice, double currentPrice, Long portfolioId, boolean isTraded,
            String record) {

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
