package com.productdistribution.backend.utils.metrics;

import com.productdistribution.backend.dtos.metrics.UnfulfilledDemandDTO;

public class UnfulfilledDemandDTOBuilder {

    private Long totalUnits = 0L;
    private Long unitsByStockShortage = 0L;
    private Long unitsByCapacityShortage = 0L;

    private UnfulfilledDemandDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static UnfulfilledDemandDTOBuilder builder() {
        return new UnfulfilledDemandDTOBuilder();
    }

    public UnfulfilledDemandDTOBuilder withTotalUnits(Long totalUnits) {
        this.totalUnits = totalUnits;
        return this;
    }

    public UnfulfilledDemandDTOBuilder withUnitsByStockShortage(Long unitsByStockShortage) {
        this.unitsByStockShortage = unitsByStockShortage;
        return this;
    }

    public UnfulfilledDemandDTOBuilder withUnitsByCapacityShortage(Long unitsByCapacityShortage) {
        this.unitsByCapacityShortage = unitsByCapacityShortage;
        return this;
    }

    public UnfulfilledDemandDTO build() {
        return new UnfulfilledDemandDTO(totalUnits, unitsByStockShortage, unitsByCapacityShortage);
    }

    public static UnfulfilledDemandDTO defaultUnfulfilledDemand() {
        return builder().build();
    }

    public static UnfulfilledDemandDTO unfulfilledDemand1() {
        return builder()
                .withTotalUnits(50L)
                .withUnitsByStockShortage(30L)
                .withUnitsByCapacityShortage(20L)
                .build();
    }

    public static UnfulfilledDemandDTO unfulfilledDemandWhenNoFilters() {
        return builder()
                .withTotalUnits(35L)
                .withUnitsByStockShortage(15L)
                .withUnitsByCapacityShortage(20L)
                .build();
    }
}