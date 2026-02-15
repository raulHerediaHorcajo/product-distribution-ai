package com.productdistribution.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stores")
public class Store {
    @Id
    private String id;

    private Double latitude;
    private Double longitude;
    private String country;
    private Integer maxStockCapacity;
    @JsonProperty("expected_return_rate")
    private Double expectedReturnRate;
    
    private Integer remainingCapacity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private List<ProductItem> demand;

    public int calculateAdjustedDemand(int originalDemand) {
        return (int) Math.ceil(originalDemand * (1 - this.expectedReturnRate));
    }

    public boolean hasCapacityFor(int quantity) {
        return this.remainingCapacity >= quantity;
    }

    public boolean tryAllocateCapacity(int quantity) {
        if (!hasCapacityFor(quantity)) {
            return false;
        }
        this.remainingCapacity -= quantity;
        return true;
    }

    @JsonSetter("max_stock_capacity")
    public void setMaxStockCapacity(Integer maxStockCapacity) {
        this.maxStockCapacity = maxStockCapacity;
        this.remainingCapacity = maxStockCapacity;
    }
}