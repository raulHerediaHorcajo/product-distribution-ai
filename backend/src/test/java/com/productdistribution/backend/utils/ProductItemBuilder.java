package com.productdistribution.backend.utils;

import com.productdistribution.backend.entities.ProductItem;

import java.util.UUID;

public class ProductItemBuilder {

    private UUID id = null;
    private String productId = "P0";
    private String size = "S";
    private Integer quantity = 10;

    private ProductItemBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static ProductItemBuilder builder() {
        return new ProductItemBuilder();
    }

    public ProductItemBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public ProductItemBuilder withProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public ProductItemBuilder withSize(String size) {
        this.size = size;
        return this;
    }

    public ProductItemBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductItem build() {
        ProductItem productItem = new ProductItem();
        productItem.setId(id);
        productItem.setProductId(productId);
        productItem.setSize(size);
        productItem.setQuantity(quantity);
        return productItem;
    }

    public static ProductItem defaultProductItemWithoutId() {
        return builder().build();
    }

    public static ProductItem defaultProductItem() {
        ProductItem productItem = defaultProductItemWithoutId();
        productItem.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        return productItem;
    }

    public static ProductItem productItem1WithoutId() {
        return builder()
                .withProductId("P1")
                .withSize("M")
                .withQuantity(50)
                .build();
    }

    public static ProductItem productItem1() {
        ProductItem productItem = productItem1WithoutId();
        productItem.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        return productItem;
    }

    public static ProductItem productItem2WithoutId() {
        return builder()
                .withProductId("P2")
                .withSize("L")
                .withQuantity(30)
                .build();
    }

    public static ProductItem productItem2() {
        ProductItem productItem = productItem2WithoutId();
        productItem.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        return productItem;
    }
}