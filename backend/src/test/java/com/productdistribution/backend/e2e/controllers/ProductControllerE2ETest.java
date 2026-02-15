package com.productdistribution.backend.e2e.controllers;

import com.productdistribution.backend.dtos.ProductDTO;
import com.productdistribution.backend.utils.ProductDTOBuilder;
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
class ProductControllerE2ETest {

    @Autowired
    private WebTestClient webTestClient;

    private static List<ProductDTO> expectedProducts;

    @BeforeAll
    static void setUp() {
        expectedProducts = List.of(
                ProductDTOBuilder.builder().withId("P1").withBrandId("B1")
                        .withSizes(List.of("M")).build(),
                ProductDTOBuilder.builder().withId("P2").withBrandId("B2")
                        .withSizes(List.of("M")).build(),
                ProductDTOBuilder.builder().withId("P3").withBrandId("B3")
                        .withSizes(List.of("M")).build()
        );
    }

    @Test
    void getAllProducts_shouldReturn200WithProductList() {
        webTestClient.get()
                .uri("/api/products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(ProductDTO.class)
                .value(products -> {
                    assertThat(products).hasSize(expectedProducts.size());
                    assertThat(products).usingRecursiveComparison()
                            .ignoringCollectionOrder().isEqualTo(expectedProducts);
                });
    }

    @Test
    void getProductById_whenProductExists_shouldReturn200WithProduct() {
        ProductDTO expectedProduct = expectedProducts.get(0);

        webTestClient.get()
                .uri("/api/products/{id}", expectedProduct.id())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(ProductDTO.class)
                .value(product -> {
                    assertThat(product).isEqualTo(expectedProduct);
                });
    }

    @Test
    void getProductById_whenProductNotFound_shouldReturn404() {
        String nonExistentId = "NON_EXISTENT";

        webTestClient.get()
                .uri("/api/products/{id}", nonExistentId)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Product with id 'NON_EXISTENT' not found")
                .jsonPath("$.path").isEqualTo("/api/products/NON_EXISTENT");
    }
}