package com.productdistribution.backend.dtos;

import java.util.List;

public record WarehouseDTO(
    String id,
    Double latitude,
    Double longitude,
    String country,
    List<ProductItemDTO> stock
) {}