package com.productdistribution.backend.utils;

import com.productdistribution.backend.dtos.ProductItemDTO;

import java.util.UUID;

public class ProductItemDTOBuilder {

    private UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private String productId = "P0";
    private String size = "S";
    private Integer quantity = 10;

    private ProductItemDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static ProductItemDTOBuilder builder() {
        return new ProductItemDTOBuilder();
    }

    public ProductItemDTOBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public ProductItemDTOBuilder withProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public ProductItemDTOBuilder withSize(String size) {
        this.size = size;
        return this;
    }

    public ProductItemDTOBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductItemDTO build() {
        return new ProductItemDTO(id, productId, size, quantity);
    }

    public static ProductItemDTO defaultProductItemDTO() {
        return builder().build();
    }

    public static ProductItemDTO productItemDTO1() {
        UUID id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return builder()
                .withId(id1)
                .withProductId("P1")
                .withSize("M")
                .withQuantity(50)
                .build();
    }

    public static ProductItemDTO productItemDTO2() {
        UUID id2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        return builder()
                .withId(id2)
                .withProductId("P2")
                .withSize("L")
                .withQuantity(30)
                .build();
    }
}