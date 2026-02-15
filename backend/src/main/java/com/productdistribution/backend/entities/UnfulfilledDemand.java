package com.productdistribution.backend.entities;

import com.productdistribution.backend.enums.UnfulfilledReason;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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
@Table(name = "unfulfilled_demands")
public class UnfulfilledDemand {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String storeId;
    private String productId;
    private String size;
    private Integer quantityMissing;
    @Enumerated(EnumType.STRING)
    private UnfulfilledReason reason;

    public UnfulfilledDemand(String storeId, String productId, String size, Integer quantityMissing, UnfulfilledReason reason) {
        this.storeId = storeId;
        this.productId = productId;
        this.size = size;
        this.quantityMissing = quantityMissing;
        this.reason = reason;
    }
}