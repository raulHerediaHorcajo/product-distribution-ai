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
@Table(name = "product_items")
public class ProductItem {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String productId;
    private String size;
    private Integer quantity;

    public void reduceQuantity(int amount) {
        if (amount > this.quantity) {
            throw new IllegalArgumentException("Cannot reduce by more than available. Available: " + this.quantity + ", requested: " + amount);
        }
        this.quantity -= amount;
    }

    public boolean hasStock() {
        return this.quantity > 0;
    }
}