package com.productdistribution.backend.utils.metrics;

import com.productdistribution.backend.dtos.metrics.DistanceStatsDTO;

public class DistanceStatsDTOBuilder {

    private Double minDistance = 0.0;
    private Double maxDistance = 0.0;
    private Double medianDistance = 0.0;

    private DistanceStatsDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static DistanceStatsDTOBuilder builder() {
        return new DistanceStatsDTOBuilder();
    }

    public DistanceStatsDTOBuilder withMinDistance(Double minDistance) {
        this.minDistance = minDistance;
        return this;
    }

    public DistanceStatsDTOBuilder withMaxDistance(Double maxDistance) {
        this.maxDistance = maxDistance;
        return this;
    }

    public DistanceStatsDTOBuilder withMedianDistance(Double medianDistance) {
        this.medianDistance = medianDistance;
        return this;
    }

    public DistanceStatsDTO build() {
        return new DistanceStatsDTO(minDistance, maxDistance, medianDistance);
    }

    public static DistanceStatsDTO defaultDistanceStats() {
        return builder().build();
    }

    public static DistanceStatsDTO distanceStats1() {
        return builder()
                .withMinDistance(100.0)
                .withMaxDistance(200.0)
                .withMedianDistance(150.0)
                .build();
    }

    public static DistanceStatsDTO distanceStatsWhenNoFilters() {
        return builder()
                .withMinDistance(100.0)
                .withMaxDistance(150.0)
                .withMedianDistance(125.0)
                .build();
    }
}