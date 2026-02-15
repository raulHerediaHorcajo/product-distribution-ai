package com.productdistribution.backend.utils;

import com.productdistribution.backend.entities.UnfulfilledDemand;
import com.productdistribution.backend.enums.UnfulfilledReason;

import java.util.UUID;

public class UnfulfilledDemandBuilder {

    private UUID id = null;
    private String storeId = "S0";
    private String productId = "P0";
    private String size = "S";
    private Integer quantityMissing = 10;
    private UnfulfilledReason reason = UnfulfilledReason.STOCK_SHORTAGE;

    private UnfulfilledDemandBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static UnfulfilledDemandBuilder builder() {
        return new UnfulfilledDemandBuilder();
    }

    public UnfulfilledDemandBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public UnfulfilledDemandBuilder withStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public UnfulfilledDemandBuilder withProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public UnfulfilledDemandBuilder withSize(String size) {
        this.size = size;
        return this;
    }

    public UnfulfilledDemandBuilder withQuantityMissing(Integer quantityMissing) {
        this.quantityMissing = quantityMissing;
        return this;
    }

    public UnfulfilledDemandBuilder withReason(UnfulfilledReason reason) {
        this.reason = reason;
        return this;
    }

    public UnfulfilledDemand build() {
        UnfulfilledDemand unfulfilledDemand = new UnfulfilledDemand(storeId, productId, size, quantityMissing, reason);
        unfulfilledDemand.setId(id);
        return unfulfilledDemand;
    }

    public static UnfulfilledDemand defaultUnfulfilledDemandWithoutId() {
        return builder().build();
    }

    public static UnfulfilledDemand defaultUnfulfilledDemand() {
        UnfulfilledDemand unfulfilledDemand = defaultUnfulfilledDemandWithoutId();
        unfulfilledDemand.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        return unfulfilledDemand;
    }

    public static UnfulfilledDemand unfulfilledDemand1WithoutId() {
        return builder()
                .withStoreId("S1")
                .withProductId("P1")
                .withSize("M")
                .withQuantityMissing(15)
                .withReason(UnfulfilledReason.STOCK_SHORTAGE)
                .build();
    }

    public static UnfulfilledDemand unfulfilledDemand1() {
        UnfulfilledDemand unfulfilledDemand = unfulfilledDemand1WithoutId();
        unfulfilledDemand.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        return unfulfilledDemand;
    }

    public static UnfulfilledDemand unfulfilledDemand2WithoutId() {
        return builder()
                .withStoreId("S2")
                .withProductId("P2")
                .withSize("L")
                .withQuantityMissing(20)
                .withReason(UnfulfilledReason.CAPACITY_SHORTAGE)
                .build();
    }

    public static UnfulfilledDemand unfulfilledDemand2() {
        UnfulfilledDemand unfulfilledDemand = unfulfilledDemand2WithoutId();
        unfulfilledDemand.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        return unfulfilledDemand;
    }
}