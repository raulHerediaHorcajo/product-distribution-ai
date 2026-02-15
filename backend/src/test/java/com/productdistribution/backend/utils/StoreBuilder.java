package com.productdistribution.backend.utils;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class StoreBuilder {

    private String id = "S0";
    private Double latitude = 40.4168;
    private Double longitude = -3.7038;
    private String country = "ES";
    private Integer maxStockCapacity = 100;
    private Double expectedReturnRate = 0.1;
    private Integer remainingCapacity = 100;
    private List<ProductItem> demand = List.of(ProductItemBuilder.defaultProductItemWithoutId());

    private StoreBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static StoreBuilder builder() {
        return new StoreBuilder();
    }

    public StoreBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public StoreBuilder withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public StoreBuilder withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public StoreBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public StoreBuilder withMaxStockCapacity(Integer maxStockCapacity) {
        this.maxStockCapacity = maxStockCapacity;
        return this;
    }

    public StoreBuilder withExpectedReturnRate(Double expectedReturnRate) {
        this.expectedReturnRate = expectedReturnRate;
        return this;
    }

    public StoreBuilder withRemainingCapacity(Integer remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
        return this;
    }

    public StoreBuilder withDemand(List<ProductItem> demand) {
        this.demand = demand != null ? new ArrayList<>(demand) : null;
        return this;
    }

    public Store build() {
        Store store = new Store();
        store.setId(id);
        store.setLatitude(latitude);
        store.setLongitude(longitude);
        store.setCountry(country);
        store.setMaxStockCapacity(maxStockCapacity);
        store.setExpectedReturnRate(expectedReturnRate);
        store.setRemainingCapacity(remainingCapacity);
        store.setDemand(demand);
        return store;
    }

    public static Store defaultStoreWithoutProductItemIds() {
        return builder().build();
    }

    public static Store defaultStore() {
        Store store = defaultStoreWithoutProductItemIds();
        store.setDemand(List.of(
            ProductItemBuilder.defaultProductItem()
        ));
        return store;
    }

    public static Store store1WithoutProductItemIds() {
        return builder()
                .withId("S1")
                .withLatitude(48.8566)
                .withLongitude(2.3522)
                .withCountry("FR")
                .withMaxStockCapacity(100)
                .withExpectedReturnRate(0.1)
                .withRemainingCapacity(100)
                .withDemand(List.of(
                    ProductItemBuilder.productItem1WithoutId(),
                    ProductItemBuilder.productItem2WithoutId()
                ))
                .build();
    }

    public static Store store1() {
        Store store = store1WithoutProductItemIds();
        store.setDemand(List.of(
            ProductItemBuilder.productItem1(),
            ProductItemBuilder.productItem2()
        ));
        return store;
    }

    public static Store store2WithoutProductItemIds() {
        return builder()
                .withId("S2")
                .withLatitude(41.3851)
                .withLongitude(2.1734)
                .withCountry("ES")
                .withMaxStockCapacity(200)
                .withExpectedReturnRate(0.15)
                .withRemainingCapacity(180)
                .withDemand(List.of(
                    ProductItemBuilder.productItem1WithoutId()
                ))
                .build();
    }

    public static Store store2() {
        Store store = store2WithoutProductItemIds();
        store.setDemand(List.of(
            ProductItemBuilder.productItem1()
        ));
        return store;
    }
}