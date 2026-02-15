package com.productdistribution.backend.dtos;

import java.util.List;

public record StoreDTO(
    String id,
    Double latitude,
    Double longitude,
    String country,
    Integer maxStockCapacity,
    Double expectedReturnRate,
    Integer remainingCapacity,
    List<ProductItemDTO> demand
) {}