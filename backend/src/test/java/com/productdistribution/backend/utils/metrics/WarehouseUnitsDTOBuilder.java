package com.productdistribution.backend.utils.metrics;

import com.productdistribution.backend.dtos.metrics.WarehouseUnitsDTO;

public class WarehouseUnitsDTOBuilder {

    private String warehouseId = "W0";
    private Long totalUnits = 0L;
    private Double percentage = 0.0;
    private Double avgDistance = 0.0;

    private WarehouseUnitsDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static WarehouseUnitsDTOBuilder builder() {
        return new WarehouseUnitsDTOBuilder();
    }

    public WarehouseUnitsDTOBuilder withWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
        return this;
    }

    public WarehouseUnitsDTOBuilder withTotalUnits(Long totalUnits) {
        this.totalUnits = totalUnits;
        return this;
    }

    public WarehouseUnitsDTOBuilder withPercentage(Double percentage) {
        this.percentage = percentage;
        return this;
    }

    public WarehouseUnitsDTOBuilder withAvgDistance(Double avgDistance) {
        this.avgDistance = avgDistance;
        return this;
    }

    public WarehouseUnitsDTO build() {
        return new WarehouseUnitsDTO(warehouseId, totalUnits, percentage, avgDistance);
    }

    public static WarehouseUnitsDTO defaultWarehouseUnits() {
        return builder().build();
    }

    public static WarehouseUnitsDTO warehouseUnits1() {
        return builder()
                .withWarehouseId("W1")
                .withTotalUnits(100L)
                .withPercentage(50.0)
                .withAvgDistance(1500.0)
                .build();
    }

    public static WarehouseUnitsDTO warehouseUnits2() {
        return builder()
                .withWarehouseId("W2")
                .withTotalUnits(80L)
                .withPercentage(40.0)
                .withAvgDistance(1415.625)
                .build();
    }

    public static WarehouseUnitsDTO warehouseUnitsWhenNoFilters() {
        return builder()
                .withWarehouseId("W1")
                .withTotalUnits(92L)
                .withPercentage(100.0)
                .withAvgDistance(125.0)
                .build();
    }
}