package com.productdistribution.backend.utils.metrics;

import com.productdistribution.backend.dtos.metrics.StoresServedDTO;

public class StoresServedDTOBuilder {

    private Integer servedStores = 0;
    private Integer fullyServedStores = 0;
    private Integer neverServedStores = 0;
    private Double coveragePercentage = 0.0;
    private Double fullyServedPercentage = 0.0;

    private StoresServedDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static StoresServedDTOBuilder builder() {
        return new StoresServedDTOBuilder();
    }

    public StoresServedDTOBuilder withServedStores(Integer servedStores) {
        this.servedStores = servedStores;
        return this;
    }

    public StoresServedDTOBuilder withFullyServedStores(Integer fullyServedStores) {
        this.fullyServedStores = fullyServedStores;
        return this;
    }

    public StoresServedDTOBuilder withNeverServedStores(Integer neverServedStores) {
        this.neverServedStores = neverServedStores;
        return this;
    }

    public StoresServedDTOBuilder withCoveragePercentage(Double coveragePercentage) {
        this.coveragePercentage = coveragePercentage;
        return this;
    }

    public StoresServedDTOBuilder withFullyServedPercentage(Double fullyServedPercentage) {
        this.fullyServedPercentage = fullyServedPercentage;
        return this;
    }

    public StoresServedDTO build() {
        return new StoresServedDTO(servedStores, fullyServedStores, neverServedStores, coveragePercentage, fullyServedPercentage);
    }

    public static StoresServedDTO defaultStoresServed() {
        return builder().build();
    }

    public static StoresServedDTO storesServed1() {
        return builder()
                .withServedStores(10)
                .withFullyServedStores(8)
                .withNeverServedStores(2)
                .withCoveragePercentage(80.0)
                .withFullyServedPercentage(64.0)
                .build();
    }

    public static StoresServedDTO storesServedWhenNoFilters() {
        return builder()
                .withServedStores(2)
                .withFullyServedStores(0)
                .withNeverServedStores(0)
                .withCoveragePercentage(100.0)
                .withFullyServedPercentage(0.0)
                .build();
    }
}