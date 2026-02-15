package com.productdistribution.backend.e2e.controllers;

import com.productdistribution.backend.dtos.WarehouseDTO;
import com.productdistribution.backend.utils.ProductItemDTOBuilder;
import com.productdistribution.backend.utils.WarehouseDTOBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "e2e"})
@AutoConfigureWebTestClient
@Transactional
class WarehouseControllerE2ETest {

    @Autowired
    private WebTestClient webTestClient;

    private static List<WarehouseDTO> expectedWarehouses;

    @BeforeAll
    static void setUp() {
        expectedWarehouses = List.of(
                WarehouseDTOBuilder.builder().withId("W1")
                        .withLatitude(44.913501).withLongitude(-3.7038).withCountry("ES")
                        .withStock(List.of(
                                ProductItemDTOBuilder.builder().withProductId("P1")
                                        .withSize("M").withQuantity(30).build()
                        )).build(),
                WarehouseDTOBuilder.builder().withId("W2")
                        .withLatitude(45.273137).withLongitude(-3.7038).withCountry("ES")
                        .withStock(List.of(
                                ProductItemDTOBuilder.builder().withProductId("P1")
                                        .withSize("M").withQuantity(0).build()
                        )).build(),
                WarehouseDTOBuilder.builder().withId("W3")
                        .withLatitude(49.4100).withLongitude(-3.735).withCountry("GB")
                        .withStock(List.of(
                                ProductItemDTOBuilder.builder().withProductId("P2")
                                        .withSize("M").withQuantity(20).build()
                        )).build()
        );
    }

    @Test
    void getAllWarehouses_shouldReturn200WithWarehouseList() {
        webTestClient.get()
                .uri("/api/warehouses")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(WarehouseDTO.class)
                .value(warehouses -> {
                    assertThat(warehouses).hasSize(expectedWarehouses.size());
                    assertThat(warehouses).usingRecursiveComparison()
                            .ignoringFields("stock.id")
                            .ignoringCollectionOrder()
                            .isEqualTo(expectedWarehouses);
                });
    }

    @Test
    void getWarehouseById_whenWarehouseExists_shouldReturn200WithWarehouse() {
        WarehouseDTO expectedWarehouse = expectedWarehouses.get(0);

        webTestClient.get()
                .uri("/api/warehouses/{id}", expectedWarehouse.id())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(WarehouseDTO.class)
                .value(warehouse -> {
                    assertThat(warehouse).usingRecursiveComparison()
                            .ignoringFields("stock.id")
                            .isEqualTo(expectedWarehouse);
                });
    }

    @Test
    void getWarehouseById_whenWarehouseNotFound_shouldReturn404() {
        String nonExistentId = "NON_EXISTENT";

        webTestClient.get()
                .uri("/api/warehouses/{id}", nonExistentId)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Warehouse with id 'NON_EXISTENT' not found")
                .jsonPath("$.path").isEqualTo("/api/warehouses/NON_EXISTENT");
    }
}