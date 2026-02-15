package com.productdistribution.backend.integration.repositories;

import com.productdistribution.backend.entities.Product;
import com.productdistribution.backend.repositories.ProductRepository;
import com.productdistribution.backend.utils.ProductBuilder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void save_shouldPersistProduct() {
        Product product = ProductBuilder.product1();

        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(product);
    }

    @Test
    void save_shouldPersistSizesList() {
        Product product = ProductBuilder.builder()
                .withSizes(List.of("S", "M", "L")).build();

        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSizes()).hasSize(3);
        assertThat(found.get().getSizes()).containsExactly("S", "M", "L");
    }

    @Test
    void save_whenSizesIsNull_shouldPersistProductWithNullSizes() {
        Product product = ProductBuilder.builder()
                .withSizes(null).build();

        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSizes()).isNull();
    }

    @Test
    void findById_whenProductExists_shouldReturnProduct() {
        Product product = ProductBuilder.product2();
        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(product);
    }

    @Test
    void findById_whenProductNotExists_shouldReturnEmpty() {
        Optional<Product> found = productRepository.findById("NON_EXISTENT");

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_whenProductsExist_shouldReturnAllProducts() {
        List<Product> products = List.of(ProductBuilder.product1(), ProductBuilder.product2());
        productRepository.saveAll(products);

        List<Product> found = productRepository.findAll();

        assertThat(found).usingRecursiveComparison().isEqualTo(products);
    }

    @Test
    void findAllByOrderByIdAsc_whenProductsAreUnordered_shouldReturnAllSorted() {
        List<Product> products = List.of(ProductBuilder.product2(), ProductBuilder.product1());
        productRepository.saveAll(products);
        List<Product> expectedProducts = List.of(ProductBuilder.product1(), ProductBuilder.product2());

        List<Product> found = productRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        assertThat(found).usingRecursiveComparison().isEqualTo(expectedProducts);
    }

    @Test
    void findAll_whenNoProducts_shouldReturnEmptyList() {
        List<Product> found = productRepository.findAll();

        assertThat(found).isEmpty();
    }

    @Test
    void saveAll_shouldPersistMultipleProducts() {
        List<Product> products = List.of(ProductBuilder.product2(), ProductBuilder.product1());

        productRepository.saveAll(products);

        List<Product> found = productRepository.findAll();
        assertThat(found).usingRecursiveComparison().isEqualTo(products);
    }

    @Test
    void truncateAll_shouldRemoveAllProducts() {
        List<Product> products = List.of(ProductBuilder.product1(), ProductBuilder.product2());
        productRepository.saveAll(products);

        productRepository.truncateAll();

        List<Product> found = productRepository.findAll();
        assertThat(found).isEmpty();
    }
}