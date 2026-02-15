package com.productdistribution.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdistribution.backend.entities.Product;
import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.exceptions.DataLoadingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.io.IOException;
import java.util.List;

@Service
public class JsonDataLoaderService implements DataLoaderService {

    @Value("${data.products.url}")
    private String productsUrl;
    @Value("${data.stores.url}")
    private String storesUrl;
    @Value("${data.warehouses.url}")
    private String warehousesUrl;

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Autowired
    public JsonDataLoaderService(ObjectMapper objectMapper, WebClient webClient) {
        this.objectMapper = objectMapper;
        this.webClient = webClient;
    }

    @Override
    public List<Product> loadProducts() {
        return loadFromUrl(productsUrl, new TypeReference<List<Product>>() {});
    }

    @Override
    public List<Store> loadStores() {
        return loadFromUrl(storesUrl, new TypeReference<List<Store>>() {});
    }

    @Override
    public List<Warehouse> loadWarehouses() {
        return loadFromUrl(warehousesUrl, new TypeReference<List<Warehouse>>() {});
    }

    private <T> T loadFromUrl(String url, TypeReference<T> typeReference) {
        try {
            String jsonContent = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                throw new DataLoadingException("Empty response from URL: " + url);
            }
            
            return objectMapper.readValue(jsonContent, typeReference);
        } catch (WebClientException ex) {
            throw new DataLoadingException("HTTP error loading from URL: " + url, ex);
        } catch (IOException ex) {
            throw new DataLoadingException("JSON parsing error loading from URL: " + url, ex);
        }
    }
}