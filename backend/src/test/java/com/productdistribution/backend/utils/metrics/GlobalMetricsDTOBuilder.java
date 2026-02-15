package com.productdistribution.backend.utils.metrics;

import com.productdistribution.backend.dtos.metrics.GlobalMetricsDTO;

public class GlobalMetricsDTOBuilder {

    private Long totalShipments = 0L;
    private Long fulfilledUnits = 0L;
    private Long unfulfilledUnits = 0L;
    private Double averageDistance = 0.0;

    private GlobalMetricsDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static GlobalMetricsDTOBuilder builder() {
        return new GlobalMetricsDTOBuilder();
    }

    public GlobalMetricsDTOBuilder withTotalShipments(Long totalShipments) {
        this.totalShipments = totalShipments;
        return this;
    }

    public GlobalMetricsDTOBuilder withFulfilledUnits(Long fulfilledUnits) {
        this.fulfilledUnits = fulfilledUnits;
        return this;
    }

    public GlobalMetricsDTOBuilder withUnfulfilledUnits(Long unfulfilledUnits) {
        this.unfulfilledUnits = unfulfilledUnits;
        return this;
    }

    public GlobalMetricsDTOBuilder withAverageDistance(Double averageDistance) {
        this.averageDistance = averageDistance;
        return this;
    }

    public GlobalMetricsDTO build() {
        return new GlobalMetricsDTO(totalShipments, fulfilledUnits, unfulfilledUnits, averageDistance);
    }

    public static GlobalMetricsDTO defaultGlobalMetrics() {
        return builder().build();
    }

    public static GlobalMetricsDTO globalMetrics1() {
        return builder()
                .withTotalShipments(100L)
                .withFulfilledUnits(500L)
                .withUnfulfilledUnits(50L)
                .withAverageDistance(25.5)
                .build();
    }
}