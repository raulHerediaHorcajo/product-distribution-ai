package com.productdistribution.backend.utils;

import com.productdistribution.backend.dtos.StoreDTO;
import com.productdistribution.backend.dtos.ProductItemDTO;

import java.util.ArrayList;
import java.util.List;

public class StoreDTOBuilder {

    private String id = "S0";
    private Double latitude = 40.4168;
    private Double longitude = -3.7038;
    private String country = "ES";
    private Integer maxStockCapacity = 100;
    private Double expectedReturnRate = 0.1;
    private Integer remainingCapacity = 100;
    private List<ProductItemDTO> demand = List.of(ProductItemDTOBuilder.defaultProductItemDTO());

    private StoreDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static StoreDTOBuilder builder() {
        return new StoreDTOBuilder();
    }

    public StoreDTOBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public StoreDTOBuilder withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public StoreDTOBuilder withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public StoreDTOBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public StoreDTOBuilder withMaxStockCapacity(Integer maxStockCapacity) {
        this.maxStockCapacity = maxStockCapacity;
        return this;
    }

    public StoreDTOBuilder withExpectedReturnRate(Double expectedReturnRate) {
        this.expectedReturnRate = expectedReturnRate;
        return this;
    }

    public StoreDTOBuilder withRemainingCapacity(Integer remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
        return this;
    }

    public StoreDTOBuilder withDemand(List<ProductItemDTO> demand) {
        this.demand = demand != null ? new ArrayList<>(demand) : null;
        return this;
    }

    public StoreDTO build() {
        return new StoreDTO(id, latitude, longitude, country, maxStockCapacity, expectedReturnRate, remainingCapacity, demand);
    }

    public static StoreDTO defaultStoreDTO() {
        return builder().build();
    }

    public static StoreDTO storeDTO1() {
        return builder()
                .withId("S1")
                .withLatitude(48.8566)
                .withLongitude(2.3522)
                .withCountry("FR")
                .withMaxStockCapacity(100)
                .withExpectedReturnRate(0.1)
                .withRemainingCapacity(100)
                .withDemand(List.of(
                    ProductItemDTOBuilder.productItemDTO1(),
                    ProductItemDTOBuilder.productItemDTO2()
                ))
                .build();
    }

    public static StoreDTO storeDTO2() {
        return builder()
                .withId("S2")
                .withLatitude(41.3851)
                .withLongitude(2.1734)
                .withCountry("ES")
                .withMaxStockCapacity(200)
                .withExpectedReturnRate(0.15)
                .withRemainingCapacity(180)
                .withDemand(List.of(
                    ProductItemDTOBuilder.productItemDTO1()
                ))
                .build();
    }
}