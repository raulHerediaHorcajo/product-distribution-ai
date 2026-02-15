package com.productdistribution.backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JsonChangeWatcher {

    private static final Logger logger = LoggerFactory.getLogger(JsonChangeWatcher.class);
    private static final String HASH_ALGORITHM = "SHA-256";

    @Value("${data.products.url}")
    private String productsUrl;
    @Value("${data.stores.url}")
    private String storesUrl;
    @Value("${data.warehouses.url}")
    private String warehousesUrl;

    private final Map<String, String> contentHashMap = new HashMap<>();
    private final WebClient webClient;

    @Autowired
    public JsonChangeWatcher(WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean hasAnyChanged() {
        boolean productsChanged = hasChanged("products", productsUrl);
        boolean storesChanged = hasChanged("stores", storesUrl);
        boolean warehousesChanged = hasChanged("warehouses", warehousesUrl);
        return productsChanged || storesChanged || warehousesChanged;
    }

    private boolean hasChanged(String key, String url) {
        try {
            String currentHash = calculateContentHash(url);
            String previousHash = contentHashMap.get(key);

            if (previousHash == null || !previousHash.equals(currentHash)) {
                contentHashMap.put(key, currentHash);
                return true;
            }

            return false;
        } catch (Exception ex) {
            logger.error("Error checking changes for {} at URL: {}", key, url, ex);
            return false;
        }
    }

    public void updateContentHashes() {
        updateContentHash("products", productsUrl);
        updateContentHash("stores", storesUrl);
        updateContentHash("warehouses", warehousesUrl);
    }

    private void updateContentHash(String key, String url) {
        try {
            String hash = calculateContentHash(url);
            contentHashMap.put(key, hash);
        } catch (Exception ex) {
            logger.error("Error updating hash for {} at URL: {}", key, url, ex);
        }
    }

    private String calculateContentHash(String url) throws IOException, NoSuchAlgorithmException {
        byte[] content = getContentFromUrl(url);
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hashBytes = digest.digest(content);
        return bytesToHex(hashBytes);
    }

    private byte[] getContentFromUrl(String url) throws IOException {
        try {
            byte[] content = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
            
            if (content == null || content.length == 0) {
                throw new IOException("Empty response from URL: " + url);
            }
            return content;
        } catch (WebClientException ex) {
            throw new IOException("HTTP error fetching content from URL: " + url, ex);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", 0xff & b));
        }
        return hexString.toString();
    }
}