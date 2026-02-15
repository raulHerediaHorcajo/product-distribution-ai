package com.productdistribution.backend.utils;

import com.productdistribution.backend.dtos.WarehouseDTO;
import com.productdistribution.backend.dtos.ProductItemDTO;

import java.util.ArrayList;
import java.util.List;

public class WarehouseDTOBuilder {

    private String id = "W0";
    private Double latitude = 40.4168;
    private Double longitude = -3.7038;
    private String country = "ES";
    private List<ProductItemDTO> stock = List.of(ProductItemDTOBuilder.defaultProductItemDTO());

    private WarehouseDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static WarehouseDTOBuilder builder() {
        return new WarehouseDTOBuilder();
    }

    public WarehouseDTOBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public WarehouseDTOBuilder withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public WarehouseDTOBuilder withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public WarehouseDTOBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public WarehouseDTOBuilder withStock(List<ProductItemDTO> stock) {
        this.stock = stock != null ? new ArrayList<>(stock) : null;
        return this;
    }

    public WarehouseDTO build() {
        return new WarehouseDTO(id, latitude, longitude, country, stock);
    }

    public static WarehouseDTO defaultWarehouseDTO() {
        return builder().build();
    }

    public static WarehouseDTO warehouseDTO1() {
        return builder()
                .withId("W1")
                .withLatitude(48.8566)
                .withLongitude(2.3522)
                .withCountry("FR")
                .withStock(List.of(
                    ProductItemDTOBuilder.productItemDTO1(),
                    ProductItemDTOBuilder.productItemDTO2()
                ))
                .build();
    }

    public static WarehouseDTO warehouseDTO2() {
        return builder()
                .withId("W2")
                .withLatitude(41.3851)
                .withLongitude(2.1734)
                .withCountry("ES")
                .withStock(List.of(
                    ProductItemDTOBuilder.productItemDTO1()
                ))
                .build();
    }
}