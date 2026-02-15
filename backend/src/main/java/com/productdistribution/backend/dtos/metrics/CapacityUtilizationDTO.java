package com.productdistribution.backend.dtos.metrics;

public record CapacityUtilizationDTO(
    Double percentage,
    Integer storesAtCapacity,
    Integer totalStores
) {}