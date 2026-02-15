package com.productdistribution.backend.dtos;

import java.util.UUID;

public record ProductItemDTO(
    UUID id,
    String productId,
    String size,
    Integer quantity
) {}