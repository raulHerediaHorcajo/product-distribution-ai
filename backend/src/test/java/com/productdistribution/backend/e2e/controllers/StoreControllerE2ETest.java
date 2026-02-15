package com.productdistribution.backend.e2e.controllers;

import com.productdistribution.backend.dtos.StoreDTO;
import com.productdistribution.backend.utils.ProductItemDTOBuilder;
import com.productdistribution.backend.utils.StoreDTOBuilder;
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
class StoreControllerE2ETest {

    @Autowired
    private WebTestClient webTestClient;

    private static List<StoreDTO> expectedStores;

    @BeforeAll
    static void setUp() {
        expectedStores = List.of(
                StoreDTOBuilder.builder().withId("S1").withLatitude(40.4168).withLongitude(-3.7038)
                        .withCountry("ES").withMaxStockCapacity(100)
                        .withExpectedReturnRate(0.1).withRemainingCapacity(0)
                        .withDemand(List.of(
                                ProductItemDTOBuilder.builder().withProductId("P1")
                                        .withSize("M").withQuantity(100).build(),
                                ProductItemDTOBuilder.builder().withProductId("P2")
                                        .withSize("M").withQuantity(50).build()
                        )).build(),
                StoreDTOBuilder.builder().withId("S2").withLatitude(48.8566).withLongitude(2.3522)
                        .withCountry("FR").withMaxStockCapacity(100)
                        .withExpectedReturnRate(0.1).withRemainingCapacity(100)
                        .withDemand(List.of(
                                ProductItemDTOBuilder.builder().withProductId("P3")
                                        .withSize("M").withQuantity(30).build()
                        )).build()
        );
    }

    @Test
    void getAllStores_shouldReturn200WithStoreList() {
        webTestClient.get()
                .uri("/api/stores")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(StoreDTO.class)
                .value(stores -> {
                    assertThat(stores).hasSize(expectedStores.size());
                    assertThat(stores).usingRecursiveComparison()
                            .ignoringFields("demand.id")
                            .ignoringCollectionOrder()
                            .isEqualTo(expectedStores);
                });
    }

    @Test
    void getStoreById_whenStoreExists_shouldReturn200WithStore() {
        StoreDTO expectedStore = expectedStores.get(0);

        webTestClient.get()
                .uri("/api/stores/{id}", expectedStore.id())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(StoreDTO.class)
                .value(store -> {
                    assertThat(store).usingRecursiveComparison()
                            .ignoringFields("demand.id")
                            .isEqualTo(expectedStore);
                });
    }

    @Test
    void getStoreById_whenStoreNotFound_shouldReturn404() {
        String nonExistentId = "NON_EXISTENT";

        webTestClient.get()
                .uri("/api/stores/{id}", nonExistentId)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Store with id 'NON_EXISTENT' not found")
                .jsonPath("$.path").isEqualTo("/api/stores/NON_EXISTENT");
    }
}