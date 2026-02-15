package com.productdistribution.backend.services.strategies;

import com.productdistribution.backend.entities.Warehouse;

public record WarehouseWithDistance(
    Warehouse warehouse,
    double distanceKm
) {}