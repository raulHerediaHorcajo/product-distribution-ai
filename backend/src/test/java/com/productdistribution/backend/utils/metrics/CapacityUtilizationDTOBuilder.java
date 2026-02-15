package com.productdistribution.backend.utils.metrics;

import com.productdistribution.backend.dtos.metrics.CapacityUtilizationDTO;

public class CapacityUtilizationDTOBuilder {

    private Double percentage = 0.0;
    private Integer storesAtCapacity = 0;
    private Integer totalStores = 0;

    private CapacityUtilizationDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static CapacityUtilizationDTOBuilder builder() {
        return new CapacityUtilizationDTOBuilder();
    }

    public CapacityUtilizationDTOBuilder withPercentage(Double percentage) {
        this.percentage = percentage;
        return this;
    }

    public CapacityUtilizationDTOBuilder withStoresAtCapacity(Integer storesAtCapacity) {
        this.storesAtCapacity = storesAtCapacity;
        return this;
    }

    public CapacityUtilizationDTOBuilder withTotalStores(Integer totalStores) {
        this.totalStores = totalStores;
        return this;
    }

    public CapacityUtilizationDTO build() {
        return new CapacityUtilizationDTO(percentage, storesAtCapacity, totalStores);
    }

    public static CapacityUtilizationDTO defaultCapacityUtilization() {
        return builder().build();
    }

    public static CapacityUtilizationDTO capacityUtilization1() {
        return builder()
                .withPercentage(75.0)
                .withStoresAtCapacity(150)
                .withTotalStores(200)
                .build();
    }

    public static CapacityUtilizationDTO capacityUtilizationWhenNoFilters() {
        return builder()
                .withPercentage(20.0 / 300.0 * 100.0)
                .withStoresAtCapacity(0)
                .withTotalStores(2)
                .build();
    }
}