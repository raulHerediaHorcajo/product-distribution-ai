package com.productdistribution.backend.integration.services;

import com.productdistribution.backend.entities.Product;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.repositories.ProductRepository;
import com.productdistribution.backend.services.DataLoaderService;
import com.productdistribution.backend.services.ProductService;
import com.productdistribution.backend.utils.ProductBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private DataLoaderService dataLoaderService;

    @Test
    void loadProductsFromJson_shouldReturnProductsFromDataLoader() {
        List<Product> products = List.of(ProductBuilder.product1(), ProductBuilder.product2());
        when(dataLoaderService.loadProducts()).thenReturn(products);

        List<Product> result = productService.loadProductsFromJson();

        verify(dataLoaderService).loadProducts();
        assertThat(result).usingRecursiveComparison().isEqualTo(products);
    }

    @Test
    void refreshProductsFromJson_shouldDeleteAllAndLoadNewProducts() {
        Product existingProduct = ProductBuilder.product1();
        productRepository.save(existingProduct);

        List<Product> newProducts = List.of(ProductBuilder.product2());
        when(dataLoaderService.loadProducts()).thenReturn(newProducts);

        List<Product> result = productService.refreshProductsFromJson();

        verify(dataLoaderService).loadProducts();
        assertThat(result).usingRecursiveComparison().isEqualTo(newProducts);
        List<Product> found = productRepository.findAll();
        assertThat(found).usingRecursiveComparison().isEqualTo(newProducts);
        Optional<Product> foundExistingProduct = productRepository.findById(existingProduct.getId());
        assertThat(foundExistingProduct).isEmpty();
    }

    @Test
    void add_shouldSaveProduct() {
        Product product = ProductBuilder.product1();

        productService.add(product);

        Optional<Product> found = productRepository.findById(product.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(product);
    }

    @Test
    void addAll_shouldSaveAllProducts() {
        List<Product> products = List.of(ProductBuilder.product1(), ProductBuilder.product2());

        productService.addAll(products);

        List<Product> found = productRepository.findAll();
        assertThat(found).usingRecursiveComparison().isEqualTo(products);
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        List<Product> products = List.of(ProductBuilder.product1(), ProductBuilder.product2());
        productRepository.saveAll(products);

        List<Product> result = productService.getAllProducts();

        assertThat(result).usingRecursiveComparison().isEqualTo(products);
    }
    
    @Test
    void getAllProducts_whenProductsAreUnordered_shouldReturnAllSorted() {
        List<Product> products = List.of(ProductBuilder.product2(), ProductBuilder.product1());
        productRepository.saveAll(products);
        List<Product> expectedProducts = List.of(ProductBuilder.product1(), ProductBuilder.product2());

        List<Product> result = productService.getAllProducts();

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedProducts);
    }

    @Test
    void getAllProducts_whenNoProducts_shouldReturnEmptyList() {
        List<Product> result = productService.getAllProducts();

        assertThat(result).isEmpty();
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProduct() {
        Product product = ProductBuilder.product1();
        productRepository.save(product);

        Product result = productService.getProductById(product.getId());

        assertThat(result).usingRecursiveComparison().isEqualTo(product);
    }

    @Test
    void getProductById_whenProductNotFound_shouldThrowResourceNotFoundException() {
        assertThatThrownBy(() -> productService.getProductById("NON_EXISTENT"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product")
                .hasMessageContaining("NON_EXISTENT");
    }

    @Test
    void deleteAll_shouldDeleteAllProducts() {
        List<Product> products = List.of(ProductBuilder.product1(), ProductBuilder.product2());
        productRepository.saveAll(products);

        productService.deleteAll();

        List<Product> found = productRepository.findAll();
        assertThat(found).isEmpty();
    }
}