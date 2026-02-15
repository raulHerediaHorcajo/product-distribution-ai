package com.productdistribution.backend.e2e.controllers;

import com.productdistribution.backend.dtos.StockAssignmentDTO;
import com.productdistribution.backend.utils.StockAssignmentDTOBuilder;
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
class StockAssignmentControllerE2ETest {

    @Autowired
    private WebTestClient webTestClient;

    private static List<StockAssignmentDTO> expectedAllAssignments;

    @BeforeAll
    static void setUp() {
        expectedAllAssignments = List.of(
                StockAssignmentDTOBuilder.builder().withStoreId("S1").withWarehouseId("W1").withProductId("P1")
                        .withSize("M").withQuantity(20).withDistanceKm(500.0).build(),
                StockAssignmentDTOBuilder.builder().withStoreId("S1").withWarehouseId("W2").withProductId("P1")
                        .withSize("M").withQuantity(70).withDistanceKm(540.0).build(),
                StockAssignmentDTOBuilder.builder().withStoreId("S1").withWarehouseId("W3").withProductId("P2")
                        .withSize("M").withQuantity(10).withDistanceKm(1000.0).build()
        );
    }

    @Test
    void distributeProducts_shouldReturn200WithStockAssignmentList() {
        List<StockAssignmentDTO> expectedAssignments = expectedAllAssignments;

        webTestClient.post()
                .uri("/api/stock-assignments/distribute")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(StockAssignmentDTO.class)
                .value(assignments -> {
                    assertThat(assignments).hasSize(expectedAssignments.size());
                    assertThat(assignments).usingRecursiveComparison()
                            .ignoringFields("id")
                            .ignoringCollectionOrder()
                            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1, "distanceKm")
                            .isEqualTo(expectedAssignments);
                });
    }

    @Test
    void getAssignments_shouldReturn200WithStockAssignmentList() {
        List<StockAssignmentDTO> expectedAssignments = expectedAllAssignments;

        webTestClient.get()
                .uri("/api/stock-assignments")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(StockAssignmentDTO.class)
                .value(assignments -> {
                    assertThat(assignments).hasSize(expectedAssignments.size());
                    assertThat(assignments).usingRecursiveComparison()
                            .ignoringFields("id")
                            .ignoringCollectionOrder()
                            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1, "distanceKm")
                            .isEqualTo(expectedAssignments);
                });
    }

    @Test
    void getAssignments_whenCriteriaProvided_shouldReturnFilteredStockAssignments() {
        String productId = "P1";
        List<StockAssignmentDTO> expectedAssignments = List.of(expectedAllAssignments.get(0), expectedAllAssignments.get(1));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stock-assignments")
                        .queryParam("productId", productId)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(StockAssignmentDTO.class)
                .value(assignments -> {
                    assertThat(assignments).hasSize(expectedAssignments.size());
                    assertThat(assignments).usingRecursiveComparison()
                            .ignoringFields("id")
                            .ignoringCollectionOrder()
                            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1, "distanceKm")
                            .isEqualTo(expectedAssignments);
                });
    }

    @Test
    void getAssignmentsByStore_whenStoreExists_shouldReturn200WithStockAssignmentList() {
        String storeId = "S1";
        List<StockAssignmentDTO> expectedAssignments = expectedAllAssignments;

        webTestClient.get()
                .uri("/api/stock-assignments/stores/{storeId}", storeId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(StockAssignmentDTO.class)
                .value(assignments -> {
                    assertThat(assignments).hasSize(expectedAssignments.size());
                    assertThat(assignments).usingRecursiveComparison()
                            .ignoringFields("id")
                            .ignoringCollectionOrder()
                            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1, "distanceKm")
                            .isEqualTo(expectedAssignments);
                });
    }

    @Test
    void getAssignmentsByProduct_whenProductExists_shouldReturn200WithStockAssignmentList() {
        String productId = "P1";
        List<StockAssignmentDTO> expectedAssignments = List.of(expectedAllAssignments.get(0), expectedAllAssignments.get(1));

        webTestClient.get()
                .uri("/api/stock-assignments/products/{productId}", productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(StockAssignmentDTO.class)
                .value(assignments -> {
                    assertThat(assignments).hasSize(expectedAssignments.size());
                    assertThat(assignments).usingRecursiveComparison()
                            .ignoringFields("id")
                            .ignoringCollectionOrder()
                            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1, "distanceKm")
                            .isEqualTo(expectedAssignments);
                });
    }

    @Test
    void getAssignmentsByWarehouse_whenWarehouseExists_shouldReturn200WithStockAssignmentList() {
        String warehouseId = "W1";
        List<StockAssignmentDTO> expectedAssignments = List.of(expectedAllAssignments.get(0));

        webTestClient.get()
                .uri("/api/stock-assignments/warehouses/{warehouseId}", warehouseId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(StockAssignmentDTO.class)
                .value(assignments -> {
                    assertThat(assignments).hasSize(expectedAssignments.size());
                    assertThat(assignments).usingRecursiveComparison()
                            .ignoringFields("id")
                            .ignoringCollectionOrder()
                            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1, "distanceKm")
                            .isEqualTo(expectedAssignments);
                });
    }
}