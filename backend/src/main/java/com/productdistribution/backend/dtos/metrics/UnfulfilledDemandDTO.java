package com.productdistribution.backend.dtos.metrics;

public record UnfulfilledDemandDTO(
    Long totalUnits,
    Long unitsByStockShortage,
    Long unitsByCapacityShortage
) {}