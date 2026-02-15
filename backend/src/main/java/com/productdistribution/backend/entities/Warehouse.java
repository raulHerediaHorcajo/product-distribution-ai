package com.productdistribution.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    private String id;

    private Double latitude;
    private Double longitude;
    private String country;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private List<ProductItem> stock;

    public Optional<ProductItem> findStock(String productId, String size) {
        return this.stock.stream()
            .filter(item -> item.getProductId().equals(productId) 
                         && item.getSize().equals(size))
            .findFirst();
    }

    public int getStockForProduct(String productId, String size) {
        return findStock(productId, size)
            .map(ProductItem::getQuantity)
            .orElse(0);
    }
}