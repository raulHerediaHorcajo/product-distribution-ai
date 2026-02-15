package com.productdistribution.backend.dtos.metrics;

public record StoresServedDTO(
    Integer servedStores,
    Integer fullyServedStores,
    Integer neverServedStores,
    Double coveragePercentage,
    Double fullyServedPercentage
) {}