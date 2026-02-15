package com.productdistribution.backend.dtos;

import java.util.List;

public record ProductDTO(
    String id,
    String brandId,
    List<String> sizes
) {}