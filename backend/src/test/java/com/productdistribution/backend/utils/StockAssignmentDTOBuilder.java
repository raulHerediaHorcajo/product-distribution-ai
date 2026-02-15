package com.productdistribution.backend.utils;

import com.productdistribution.backend.dtos.StockAssignmentDTO;

import java.util.UUID;

public class StockAssignmentDTOBuilder {

    private UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private String storeId = "S0";
    private String warehouseId = "W0";
    private String productId = "P0";
    private String size = "S";
    private Integer quantity = 5;
    private Double distanceKm = 20.0;

    private StockAssignmentDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static StockAssignmentDTOBuilder builder() {
        return new StockAssignmentDTOBuilder();
    }

    public StockAssignmentDTOBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public StockAssignmentDTOBuilder withStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public StockAssignmentDTOBuilder withWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
        return this;
    }

    public StockAssignmentDTOBuilder withProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public StockAssignmentDTOBuilder withSize(String size) {
        this.size = size;
        return this;
    }

    public StockAssignmentDTOBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public StockAssignmentDTOBuilder withDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
        return this;
    }

    public StockAssignmentDTO build() {
        return new StockAssignmentDTO(id, storeId, warehouseId, productId, size, quantity, distanceKm);
    }

    public static StockAssignmentDTO defaultStockAssignmentDTO() {
        return builder().build();
    }

    public static StockAssignmentDTO stockAssignmentDTO1() {
        UUID id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return builder()
                .withId(id1)
                .withStoreId("S1")
                .withWarehouseId("W1")
                .withProductId("P1")
                .withSize("M")
                .withQuantity(45)
                .withDistanceKm(100.0)
                .build();
    }

    public static StockAssignmentDTO stockAssignmentDTO2() {
        UUID id2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        return builder()
                .withId(id2)
                .withStoreId("S1")
                .withWarehouseId("W1")
                .withProductId("P2")
                .withSize("L")
                .withQuantity(27)
                .withDistanceKm(100.0)
                .build();
    }
}