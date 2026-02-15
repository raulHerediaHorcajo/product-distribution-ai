package com.productdistribution.backend.unit.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdistribution.backend.entities.Product;
import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.exceptions.DataLoadingException;
import com.productdistribution.backend.services.JsonDataLoaderService;
import com.productdistribution.backend.utils.ProductBuilder;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.WarehouseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class JsonDataLoaderServiceUnitTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebClient webClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private JsonDataLoaderService jsonDataLoaderService;

    private static final String PRODUCTS_URL = "https://example.com/products.json";
    private static final String STORES_URL = "https://example.com/stores.json";
    private static final String WAREHOUSES_URL = "https://example.com/warehouses.json";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jsonDataLoaderService, "productsUrl", PRODUCTS_URL);
        ReflectionTestUtils.setField(jsonDataLoaderService, "storesUrl", STORES_URL);
        ReflectionTestUtils.setField(jsonDataLoaderService, "warehousesUrl", WAREHOUSES_URL);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void loadProducts_shouldReturnProductList() throws Exception {
        String jsonContent = "test content";
        List<Product> expectedProducts = List.of(ProductBuilder.product1());

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonContent));
        when(objectMapper.readValue(eq(jsonContent), any(TypeReference.class))).thenReturn(expectedProducts);

        List<Product> result = jsonDataLoaderService.loadProducts();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(PRODUCTS_URL);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
        verify(objectMapper).readValue(eq(jsonContent), any(TypeReference.class));
        assertThat(result).isEqualTo(expectedProducts);
    }

    @Test
    void loadStores_shouldReturnStoreList() throws Exception {
        String jsonContent = "test content";
        List<Store> expectedStores = List.of(StoreBuilder.store1());

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonContent));
        when(objectMapper.readValue(eq(jsonContent), any(TypeReference.class))).thenReturn(expectedStores);

        List<Store> result = jsonDataLoaderService.loadStores();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(STORES_URL);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
        verify(objectMapper).readValue(eq(jsonContent), any(TypeReference.class));
        assertThat(result).isEqualTo(expectedStores);
    }

    @Test
    void loadWarehouses_shouldReturnWarehouseList() throws Exception {
        String jsonContent = "test content";
        List<Warehouse> expectedWarehouses = List.of(WarehouseBuilder.warehouse1());

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonContent));
        when(objectMapper.readValue(eq(jsonContent), any(TypeReference.class))).thenReturn(expectedWarehouses);

        List<Warehouse> result = jsonDataLoaderService.loadWarehouses();

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(WAREHOUSES_URL);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
        verify(objectMapper).readValue(eq(jsonContent), any(TypeReference.class));
        assertThat(result).isEqualTo(expectedWarehouses);
    }

    @Test
    void loadProducts_whenNullResponse_shouldThrowDataLoadingException() throws Exception{
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.empty());

        assertThatThrownBy(() -> jsonDataLoaderService.loadProducts())
                .isInstanceOf(DataLoadingException.class)
                .hasMessageContaining("Empty response from URL");

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(PRODUCTS_URL);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
        verify(objectMapper, never()).readValue(anyString(), any(TypeReference.class));
    }

    @Test
    void loadProducts_whenEmptyResponse_shouldThrowDataLoadingException() throws Exception{
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(""));

        assertThatThrownBy(() -> jsonDataLoaderService.loadProducts())
                .isInstanceOf(DataLoadingException.class)
                .hasMessageContaining("Empty response from URL");

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(PRODUCTS_URL);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
        verify(objectMapper, never()).readValue(anyString(), any(TypeReference.class));
    }

    @Test
    void loadProducts_whenWebClientException_shouldThrowDataLoadingException() throws Exception{
        WebClientException exception = mock(WebClientException.class);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(exception));

        assertThatThrownBy(() -> jsonDataLoaderService.loadProducts())
                .isInstanceOf(DataLoadingException.class)
                .hasMessageContaining("HTTP error loading from URL");

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(PRODUCTS_URL);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
        verify(objectMapper, never()).readValue(anyString(), any(TypeReference.class));
    }

    @Test
    void loadProducts_whenIOException_shouldThrowDataLoadingException() throws Exception{
        String jsonContent = "invalid json";
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonContent));
        doAnswer(invocation -> {
            throw new IOException("JSON parsing error");
        }).when(objectMapper).readValue(eq(jsonContent), any(TypeReference.class));

        assertThatThrownBy(() -> jsonDataLoaderService.loadProducts())
                .isInstanceOf(DataLoadingException.class)
                .hasMessageContaining("JSON parsing error loading from URL");

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(PRODUCTS_URL);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
        verify(objectMapper).readValue(eq(jsonContent), any(TypeReference.class));
    }
}