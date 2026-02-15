package com.productdistribution.backend.utils;

import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.entities.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class WarehouseBuilder {

    private String id = "W0";
    private Double latitude = 40.4168;
    private Double longitude = -3.7038;
    private String country = "ES";
    private List<ProductItem> stock = List.of(ProductItemBuilder.defaultProductItemWithoutId());

    private WarehouseBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static WarehouseBuilder builder() {
        return new WarehouseBuilder();
    }

    public WarehouseBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public WarehouseBuilder withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public WarehouseBuilder withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public WarehouseBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public WarehouseBuilder withStock(List<ProductItem> stock) {
        this.stock = stock != null ? new ArrayList<>(stock) : null;
        return this;
    }

    public Warehouse build() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setLatitude(latitude);
        warehouse.setLongitude(longitude);
        warehouse.setCountry(country);
        warehouse.setStock(stock);
        return warehouse;
    }

    public static Warehouse defaultWarehouseWithoutProductItemIds() {
        return builder().build();
    }

    public static Warehouse defaultWarehouse() {
        Warehouse warehouse = defaultWarehouseWithoutProductItemIds();
        warehouse.setStock(List.of(ProductItemBuilder.defaultProductItem()));
        return warehouse;
    }

    public static Warehouse warehouse1WithoutProductItemIds() {
        return builder()
                .withId("W1")
                .withLatitude(48.8566)
                .withLongitude(2.3522)
                .withCountry("FR")
                .withStock(List.of(
                    ProductItemBuilder.productItem1WithoutId(),
                    ProductItemBuilder.productItem2WithoutId()
                ))
                .build();
    }

    public static Warehouse warehouse1() {
        Warehouse warehouse = warehouse1WithoutProductItemIds();
        warehouse.setStock(List.of(
            ProductItemBuilder.productItem1(),
            ProductItemBuilder.productItem2()
        ));
        return warehouse;
    }

    public static Warehouse warehouse2WithoutProductItemIds() {
        return builder()
                .withId("W2")
                .withLatitude(41.3851)
                .withLongitude(2.1734)
                .withCountry("ES")
                .withStock(List.of(
                    ProductItemBuilder.productItem1WithoutId()
                ))
                .build();
    }

    public static Warehouse warehouse2() {
        Warehouse warehouse = warehouse2WithoutProductItemIds();
        warehouse.setStock(List.of(
            ProductItemBuilder.productItem1()
        ));
        return warehouse;
    }
}