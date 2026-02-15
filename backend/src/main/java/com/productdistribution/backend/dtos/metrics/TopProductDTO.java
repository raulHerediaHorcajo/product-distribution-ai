package com.productdistribution.backend.dtos.metrics;

public record TopProductDTO(
    String productId,
    Long totalQuantity
) {}