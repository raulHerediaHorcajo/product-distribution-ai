package com.productdistribution.backend.dtos.metrics;

public record DistanceStatsDTO(
    Double minDistance,
    Double maxDistance,
    Double medianDistance
) {}