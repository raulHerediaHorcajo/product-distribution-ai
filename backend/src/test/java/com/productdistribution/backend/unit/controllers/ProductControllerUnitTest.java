package com.productdistribution.backend.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdistribution.backend.controllers.ProductController;
import com.productdistribution.backend.dtos.ProductDTO;
import com.productdistribution.backend.entities.Product;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.mappers.ProductMapper;
import com.productdistribution.backend.services.ProductService;
import com.productdistribution.backend.utils.ProductBuilder;
import com.productdistribution.backend.utils.ProductDTOBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
class ProductControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    @Test
    void getAllProducts_shouldReturn200WithProductList() throws Exception {
        List<Product> products = List.of(ProductBuilder.product1(), ProductBuilder.product2());
        List<ProductDTO> dtos = List.of(ProductDTOBuilder.productDTO1(), ProductDTOBuilder.productDTO2());
        String expectedJson = objectMapper.writeValueAsString(dtos);

        when(productService.getAllProducts()).thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(dtos);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(productService).getAllProducts();
        verify(productMapper).toDTOList(products);
    }

    @Test
    void getProductById_whenProductExists_shouldReturn200WithProduct() throws Exception {
        String productId = "P1";
        Product product = ProductBuilder.product1();
        ProductDTO dto = ProductDTOBuilder.productDTO1();
        String expectedJson = objectMapper.writeValueAsString(dto);

        when(productService.getProductById(productId)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(dto);

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(productService).getProductById(productId);
        verify(productMapper).toDTO(product);
    }

    @Test
    void getProductById_whenProductNotFound_shouldReturn404() throws Exception {
        String productId = "P999";

        when(productService.getProductById(productId))
                .thenThrow(new ResourceNotFoundException("Product", productId));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Product with id 'P999' not found"))
                .andExpect(jsonPath("$.path").value("/api/products/P999"));

        verify(productService).getProductById(productId);
        verify(productMapper, never()).toDTO(any(Product.class));
    }
}