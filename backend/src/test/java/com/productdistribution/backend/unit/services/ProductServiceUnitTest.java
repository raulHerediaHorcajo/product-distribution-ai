package com.productdistribution.backend.unit.services;

import com.productdistribution.backend.entities.Product;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.repositories.ProductRepository;
import com.productdistribution.backend.services.DataLoaderService;
import com.productdistribution.backend.services.ProductService;
import com.productdistribution.backend.utils.ProductBuilder;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DataLoaderService dataLoaderService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ProductService productService;

    @Test
    void loadProductsFromJson_shouldReturnProductsFromDataLoader() {
        List<Product> expectedProducts = List.of(ProductBuilder.product1(), ProductBuilder.product2());
        when(dataLoaderService.loadProducts()).thenReturn(expectedProducts);

        List<Product> result = productService.loadProductsFromJson();

        verify(dataLoaderService).loadProducts();
        assertThat(result).isEqualTo(expectedProducts);
    }

    @Test
    void refreshProductsFromJson_shouldDeleteAllLoadAndAddAll() {
        List<Product> products = List.of(ProductBuilder.product1(), ProductBuilder.product2());
        when(dataLoaderService.loadProducts()).thenReturn(products);
        when(productRepository.saveAll(products)).thenReturn(products);

        List<Product> result = productService.refreshProductsFromJson();

        verify(productRepository).truncateAll();
        verify(entityManager).clear();
        verify(dataLoaderService).loadProducts();
        verify(productRepository).saveAll(products);
        assertThat(result).isEqualTo(products);
    }

    @Test
    void add_shouldSaveProduct() {
        Product product = ProductBuilder.product1();

        productService.add(product);

        verify(productRepository).save(product);
    }

    @Test
    void addAll_shouldSaveAllProducts() {
        List<Product> products = List.of(ProductBuilder.product1(), ProductBuilder.product2());

        productService.addAll(products);

        verify(productRepository).saveAll(products);
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        List<Product> expectedProducts = List.of(ProductBuilder.product1(), ProductBuilder.product2());
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        when(productRepository.findAll(sort)).thenReturn(expectedProducts);

        List<Product> result = productService.getAllProducts();

        verify(productRepository).findAll(sort);
        assertThat(result).isEqualTo(expectedProducts);
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProduct() {
        String productId = "P1";
        Product expectedProduct = ProductBuilder.product1();
        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));

        Product result = productService.getProductById(productId);

        verify(productRepository).findById(productId);
        assertThat(result).isEqualTo(expectedProduct);
    }

    @Test
    void getProductById_whenProductNotFound_shouldThrowResourceNotFoundException() {
        String productId = "P999";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product with id 'P999' not found");

        verify(productRepository).findById(productId);
    }

    @Test
    void deleteAll_shouldDeleteAllProducts() {
        productService.deleteAll();

        verify(productRepository).truncateAll();
        verify(entityManager).clear();
    }
}