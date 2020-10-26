package com.cs203t5.ryverbank.portfolio;

import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.cs203t5.ryverbank.customer.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.Where;

import lombok.*;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @NotNull(message = "Must have customer id")
    @JsonIgnore
    private Long id;

    @JsonProperty("customer_id")
    Long customerId;
    // @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    // @ElementCollection
    @JsonProperty("assets")
    @Column(name = "assets")
    @Where(clause = "istraded = false")
    @JsonIgnoreProperties("traded")
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<Asset> assets;

    @JsonProperty("unrealized_gain_loss")
    private double unrealizedGainLoss;

    @JsonProperty("total_gain_loss")
    private double totalGainLoss;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "customerId", referencedColumnName = "customerId", updatable = false, insertable = false)
    // @MapsId("customerId")
    @JsonIgnore
    private Customer customer;

    /**
     * Constructs a portfolio with the customer id.
     * 
     * @param id The id of the user.
     */
    public Portfolio(Long id) {
        this.customerId = id;
        this.assets = new ArrayList<>();
    }

}
