package com.productdistribution.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stock_assignments")
public class StockAssignment {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String storeId;
    private String warehouseId;
    private String productId;
    private String size;
    private Integer quantity;
    private Double distanceKm;

    public StockAssignment(String storeId, String warehouseId, String productId, String size, Integer quantity, Double distanceKm) {
        this.storeId = storeId;
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
        this.distanceKm = distanceKm;
    }
}