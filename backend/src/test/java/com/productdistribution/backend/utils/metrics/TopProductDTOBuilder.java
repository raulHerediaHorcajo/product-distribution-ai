package com.productdistribution.backend.utils.metrics;

import com.productdistribution.backend.dtos.metrics.TopProductDTO;

public class TopProductDTOBuilder {

    private String productId = "P0";
    private Long totalQuantity = 0L;

    private TopProductDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static TopProductDTOBuilder builder() {
        return new TopProductDTOBuilder();
    }

    public TopProductDTOBuilder withProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public TopProductDTOBuilder withTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
        return this;
    }

    public TopProductDTO build() {
        return new TopProductDTO(productId, totalQuantity);
    }

    public static TopProductDTO defaultTopProduct() {
        return builder().build();
    }

    public static TopProductDTO topProduct1() {
        return builder()
                .withProductId("P1")
                .withTotalQuantity(500L)
                .build();
    }

    public static TopProductDTO topProduct2() {
        return builder()
                .withProductId("P2")
                .withTotalQuantity(300L)
                .build();
    }

    public static TopProductDTO topProductP1WhenNoFilters() {
        return builder()
                .withProductId("P1")
                .withTotalQuantity(65L)
                .build();
    }

    public static TopProductDTO topProductP2WhenNoFilters() {
        return builder()
                .withProductId("P2")
                .withTotalQuantity(27L)
                .build();
    }
}