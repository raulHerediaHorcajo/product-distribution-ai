package com.productdistribution.backend.utils;

import com.productdistribution.backend.dtos.ProductDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductDTOBuilder {

    private String id = "P0";
    private String brandId = "B0";
    private List<String> sizes = List.of("XS", "S", "M", "L", "XL", "XXL");

    private ProductDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static ProductDTOBuilder builder() {
        return new ProductDTOBuilder();
    }

    public ProductDTOBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ProductDTOBuilder withBrandId(String brandId) {
        this.brandId = brandId;
        return this;
    }

    public ProductDTOBuilder withSizes(List<String> sizes) {
        this.sizes = sizes != null ? new ArrayList<>(sizes) : null;
        return this;
    }

    public ProductDTO build() {
        return new ProductDTO(id, brandId, sizes);
    }

    public static ProductDTO defaultProductDTO() {
        return builder().build();
    }

    public static ProductDTO productDTO1() {
        return builder()
                .withId("P1")
                .withBrandId("B1")
                .withSizes(List.of("XS", "S", "M"))
                .build();
    }

    public static ProductDTO productDTO2() {
        return builder()
                .withId("P2")
                .withBrandId("B2")
                .withSizes(List.of("L", "XL", "XXL"))
                .build();
    }
}