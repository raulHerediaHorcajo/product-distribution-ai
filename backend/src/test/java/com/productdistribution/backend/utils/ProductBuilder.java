package com.productdistribution.backend.utils;

import com.productdistribution.backend.entities.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductBuilder {

    private String id = "P0";
    private String brandId = "B0";
    private List<String> sizes = List.of("XS", "S", "M", "L", "XL", "XXL");

    private ProductBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public ProductBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ProductBuilder withBrandId(String brandId) {
        this.brandId = brandId;
        return this;
    }

    public ProductBuilder withSizes(List<String> sizes) {
        this.sizes = sizes != null ? new ArrayList<>(sizes) : null;
        return this;
    }

    public Product build() {
        Product product = new Product();
        product.setId(id);
        product.setBrandId(brandId);
        product.setSizes(sizes);
        return product;
    }

    public static Product defaultProduct() {
        return builder().build();
    }

    public static Product product1() {
        return builder()
                .withId("P1")
                .withBrandId("B1")
                .withSizes(List.of("XS", "S", "M"))
                .build();
    }

    public static Product product2() {
        return builder()
                .withId("P2")
                .withBrandId("B2")
                .withSizes(List.of("L", "XL", "XXL"))
                .build();
    }
}