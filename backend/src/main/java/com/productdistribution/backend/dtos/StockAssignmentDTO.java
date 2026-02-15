package com.productdistribution.backend.dtos;

import java.util.UUID;

public record StockAssignmentDTO(
    UUID id,
    String storeId,
    String warehouseId,
    String productId,
    String size,
    Integer quantity,
    Double distanceKm
) {}