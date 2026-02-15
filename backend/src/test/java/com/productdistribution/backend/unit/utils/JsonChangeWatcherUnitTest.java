package com.productdistribution.backend.unit.utils;

import com.productdistribution.backend.utils.JsonChangeWatcher;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonChangeWatcherUnitTest {

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
    private JsonChangeWatcher jsonChangeWatcher;

    private static final String PRODUCTS_URL = "https://example.com/products.json";
    private static final String STORES_URL = "https://example.com/stores.json";
    private static final String WAREHOUSES_URL = "https://example.com/warehouses.json";

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        ReflectionTestUtils.setField(jsonChangeWatcher, "productsUrl", PRODUCTS_URL);
        ReflectionTestUtils.setField(jsonChangeWatcher, "storesUrl", STORES_URL);
        ReflectionTestUtils.setField(jsonChangeWatcher, "warehousesUrl", WAREHOUSES_URL);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void hasAnyChanged_whenNoPreviousHashes_shouldReturnTrue() {
        byte[] content = "test content".getBytes();
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(content));

        boolean result = jsonChangeWatcher.hasAnyChanged();

        verifyWebClientCalls(1);
        assertThat(result).isTrue();
    }

    @Test
    void hasAnyChanged_whenOnlyProductsChanged_shouldReturnTrue() {
        byte[] initialContent = "initial content".getBytes();
        byte[] changedContent = "changed content".getBytes();
        
        setupMockResponse(initialContent, initialContent, initialContent);
        boolean firstCheck = jsonChangeWatcher.hasAnyChanged();
        
        setupMockResponse(changedContent, initialContent, initialContent);
        boolean secondCheck = jsonChangeWatcher.hasAnyChanged();

        verifyWebClientCalls(2);
        assertThat(firstCheck).isTrue();
        assertThat(secondCheck).isTrue();
    }

    @Test
    void hasAnyChanged_whenOnlyStoresChanged_shouldReturnTrue() {
        byte[] initialContent = "initial content".getBytes();
        byte[] changedContent = "changed content".getBytes();
        
        setupMockResponse(initialContent, initialContent, initialContent);
        boolean firstCheck = jsonChangeWatcher.hasAnyChanged();
        
        setupMockResponse(initialContent, changedContent, initialContent);
        boolean secondCheck = jsonChangeWatcher.hasAnyChanged();

        verifyWebClientCalls(2);
        assertThat(firstCheck).isTrue();
        assertThat(secondCheck).isTrue();
    }

    @Test
    void hasAnyChanged_whenOnlyWarehousesChanged_shouldReturnTrue() {
        byte[] initialContent = "initial content".getBytes();
        byte[] changedContent = "changed content".getBytes();
        
        setupMockResponse(initialContent, initialContent, initialContent);
        boolean firstCheck = jsonChangeWatcher.hasAnyChanged();
        
        setupMockResponse(initialContent, initialContent, changedContent);
        boolean secondCheck = jsonChangeWatcher.hasAnyChanged();

        verifyWebClientCalls(2);
        assertThat(firstCheck).isTrue();
        assertThat(secondCheck).isTrue();
    }

    @Test
    void hasAnyChanged_whenContentUnchanged_shouldReturnFalse() {
        byte[] content = "same content".getBytes();
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(content));

        boolean firstCheck = jsonChangeWatcher.hasAnyChanged();
        boolean secondCheck = jsonChangeWatcher.hasAnyChanged();

        verifyWebClientCalls(2);
        assertThat(firstCheck).isTrue();
        assertThat(secondCheck).isFalse();
    }

    @Test
    void hasAnyChanged_whenWebClientException_shouldReturnFalse() {
        WebClientException exception = mock(WebClientException.class);
        when(responseSpec.bodyToMono(byte[].class))
                .thenReturn(Mono.error(exception));

        boolean result = jsonChangeWatcher.hasAnyChanged();

        verifyWebClientCalls(1);
        assertThat(result).isFalse();
    }

    @Test
    void hasAnyChanged_whenEmptyResponse_shouldReturnFalse() {
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(new byte[0]));

        boolean result = jsonChangeWatcher.hasAnyChanged();

        verifyWebClientCalls(1);
        assertThat(result).isFalse();
    }

    @Test
    void hasAnyChanged_whenNullResponse_shouldReturnFalse() {
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.empty());

        boolean result = jsonChangeWatcher.hasAnyChanged();

        verifyWebClientCalls(1);
        assertThat(result).isFalse();
    }

    @Test
    void updateContentHashes_shouldUpdateAllHashes() {
        byte[] content = "test content".getBytes();
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(content));

        jsonChangeWatcher.updateContentHashes();

        verifyWebClientCalls(1);
    }

    @Test
    void updateContentHashes_whenException_shouldNotThrow() {
        WebClientException exception = mock(WebClientException.class);
        when(responseSpec.bodyToMono(byte[].class))
                .thenReturn(Mono.error(exception));

        jsonChangeWatcher.updateContentHashes();

        verifyWebClientCalls(1);
    }

    private void setupMockResponse(byte[] productsContent, byte[] storesContent, byte[] warehousesContent) {
        when(responseSpec.bodyToMono(byte[].class))
                .thenReturn(Mono.just(productsContent))
                .thenReturn(Mono.just(storesContent))
                .thenReturn(Mono.just(warehousesContent));
    }

    private void verifyWebClientCalls(int executions) {
        int totalCalls = executions * 3;
        verify(webClient, times(totalCalls)).get();
        verify(requestHeadersUriSpec, times(executions)).uri(PRODUCTS_URL);
        verify(requestHeadersUriSpec, times(executions)).uri(STORES_URL);
        verify(requestHeadersUriSpec, times(executions)).uri(WAREHOUSES_URL);
        verify(requestHeadersSpec, times(totalCalls)).retrieve();
        verify(responseSpec, times(totalCalls)).bodyToMono(byte[].class);
    }
}