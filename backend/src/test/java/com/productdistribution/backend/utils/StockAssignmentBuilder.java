package com.productdistribution.backend.utils;

import com.productdistribution.backend.entities.StockAssignment;

import java.util.UUID;

public class StockAssignmentBuilder {

    private UUID id = null;
    private String storeId = "S0";
    private String warehouseId = "W0";
    private String productId = "P0";
    private String size = "S";
    private Integer quantity = 5;
    private Double distanceKm = 20.0;

    private StockAssignmentBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static StockAssignmentBuilder builder() {
        return new StockAssignmentBuilder();
    }

    public StockAssignmentBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public StockAssignmentBuilder withStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public StockAssignmentBuilder withWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
        return this;
    }

    public StockAssignmentBuilder withProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public StockAssignmentBuilder withSize(String size) {
        this.size = size;
        return this;
    }

    public StockAssignmentBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public StockAssignmentBuilder withDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
        return this;
    }

    public StockAssignment build() {
        StockAssignment stockAssignment = new StockAssignment(storeId, warehouseId, productId, size, quantity, distanceKm);
        stockAssignment.setId(id);
        return stockAssignment;
    }

    public static StockAssignment defaultStockAssignmentWithoutId() {
        return builder().build();
    }

    public static StockAssignment defaultStockAssignment() {
        StockAssignment stockAssignment = defaultStockAssignmentWithoutId();
        stockAssignment.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        return stockAssignment;
    }

    public static StockAssignment stockAssignment1WithoutId() {
        return builder()
                .withStoreId("S1")
                .withWarehouseId("W1")
                .withProductId("P1")
                .withSize("M")
                .withQuantity(45)
                .withDistanceKm(100.0)
                .build();
    }

    public static StockAssignment stockAssignment1() {
        StockAssignment stockAssignment = stockAssignment1WithoutId();
        stockAssignment.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        return stockAssignment;
    }

    public static StockAssignment stockAssignment2WithoutId() {
        return builder()
                .withStoreId("S1")
                .withWarehouseId("W1")
                .withProductId("P2")
                .withSize("L")
                .withQuantity(27)
                .withDistanceKm(100.0)
                .build();
    }

    public static StockAssignment stockAssignment2() {
        StockAssignment stockAssignment = stockAssignment2WithoutId();
        stockAssignment.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        return stockAssignment;
    }

    public static StockAssignment stockAssignment3WithoutId() {
        return builder()
                .withStoreId("S2")
                .withWarehouseId("W1")
                .withProductId("P1")
                .withSize("M")
                .withQuantity(20)
                .withDistanceKm(150.0)
                .build();
    }

    public static StockAssignment stockAssignment3() {
        StockAssignment stockAssignment = stockAssignment3WithoutId();
        stockAssignment.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        return stockAssignment;
    }
}