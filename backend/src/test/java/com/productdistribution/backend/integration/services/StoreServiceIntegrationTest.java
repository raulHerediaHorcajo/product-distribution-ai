package com.productdistribution.backend.integration.services;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.services.DataLoaderService;
import com.productdistribution.backend.services.StoreService;
import com.productdistribution.backend.utils.StoreBuilder;
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
class StoreServiceIntegrationTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    @MockitoBean
    private DataLoaderService dataLoaderService;

    @Test
    void loadStoresFromJson_shouldReturnStoresFromDataLoader() {
        List<Store> stores = List.of(StoreBuilder.store1WithoutProductItemIds(), StoreBuilder.store2WithoutProductItemIds());
        when(dataLoaderService.loadStores()).thenReturn(stores);

        List<Store> result = storeService.loadStoresFromJson();

        verify(dataLoaderService).loadStores();
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("demand.id").isEqualTo(stores);
    }

    @Test
    void refreshStoresFromJson_shouldDeleteAllAndLoadNewStores() {
        Store existingStore = StoreBuilder.store1WithoutProductItemIds();
        storeRepository.save(existingStore);

        List<Store> newStores = List.of(StoreBuilder.store2WithoutProductItemIds());
        when(dataLoaderService.loadStores()).thenReturn(newStores);

        List<Store> result = storeService.refreshStoresFromJson();

        verify(dataLoaderService).loadStores();
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("demand.id").isEqualTo(newStores);
        List<Store> found = storeRepository.findAll();
        assertThat(found).usingRecursiveComparison()
                .ignoringFields("demand.id").isEqualTo(newStores);
        Optional<Store> foundExistingStore = storeRepository.findById(existingStore.getId());
        assertThat(foundExistingStore).isEmpty();
    }

    @Test
    void add_shouldSaveStore() {
        Store store = StoreBuilder.store1WithoutProductItemIds();

        storeService.add(store);

        Optional<Store> found = storeRepository.findById(store.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison()
                .ignoringFields("demand.id").isEqualTo(store);
    }

    @Test
    void addAll_shouldSaveAllStores() {
        List<Store> stores = List.of(StoreBuilder.store1WithoutProductItemIds(), StoreBuilder.store2WithoutProductItemIds());

        storeService.addAll(stores);

        List<Store> found = storeRepository.findAll();
        assertThat(found).usingRecursiveComparison()
                .ignoringFields("demand.id").isEqualTo(stores);
    }

    @Test
    void getAllStores_shouldReturnAllStores() {
        List<Store> stores = List.of(StoreBuilder.store1WithoutProductItemIds(), StoreBuilder.store2WithoutProductItemIds());
        storeRepository.saveAll(stores);

        List<Store> result = storeService.getAllStores();

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("demand.id").isEqualTo(stores);
    }

    @Test
    void getAllStores_whenStoresAreUnordered_shouldReturnAllSorted() {
        List<Store> stores = List.of(StoreBuilder.store2WithoutProductItemIds(), StoreBuilder.store1WithoutProductItemIds());
        storeRepository.saveAll(stores);
        List<Store> expectedStores = List.of(StoreBuilder.store1WithoutProductItemIds(), StoreBuilder.store2WithoutProductItemIds());

        List<Store> result = storeService.getAllStores();
        
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("demand.id").isEqualTo(expectedStores);
    }

    @Test
    void getAllStores_whenNoStores_shouldReturnEmptyList() {
        List<Store> result = storeService.getAllStores();

        assertThat(result).isEmpty();
    }

    @Test
    void getStoreById_whenStoreExists_shouldReturnStore() {
        Store store = StoreBuilder.store1WithoutProductItemIds();
        storeRepository.save(store);

        Store result = storeService.getStoreById(store.getId());

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("demand.id").isEqualTo(store);
    }

    @Test
    void getStoreById_whenStoreNotFound_shouldThrowResourceNotFoundException() {
        assertThatThrownBy(() -> storeService.getStoreById("NON_EXISTENT"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Store")
                .hasMessageContaining("NON_EXISTENT");
    }

    @Test
    void deleteAll_shouldDeleteAllStores() {
        List<Store> stores = List.of(StoreBuilder.store1WithoutProductItemIds(), StoreBuilder.store2WithoutProductItemIds());
        storeRepository.saveAll(stores);

        storeService.deleteAll();

        List<Store> found = storeRepository.findAll();
        assertThat(found).isEmpty();
    }
}