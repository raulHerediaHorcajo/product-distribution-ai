package com.productdistribution.backend.dtos.metrics;

public record GlobalMetricsDTO(
    Long totalShipments,
    Long fulfilledUnits,
    Long unfulfilledUnits,
    Double averageDistance
) {}