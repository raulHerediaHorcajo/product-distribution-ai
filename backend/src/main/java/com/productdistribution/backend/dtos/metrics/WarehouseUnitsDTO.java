package com.productdistribution.backend.dtos.metrics;

public record WarehouseUnitsDTO(
    String warehouseId,
    Long totalUnits,
    Double percentage,
    Double avgDistance
) {}