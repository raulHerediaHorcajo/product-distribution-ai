package com.productdistribution.backend.unit.services;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.services.DataLoaderService;
import com.productdistribution.backend.services.StoreService;
import com.productdistribution.backend.utils.StoreBuilder;

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
class StoreServiceUnitTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private DataLoaderService dataLoaderService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private StoreService storeService;

    @Test
    void loadStoresFromJson_shouldReturnStoresFromDataLoader() {
        List<Store> expectedStores = List.of(StoreBuilder.store1(), StoreBuilder.store2());
        when(dataLoaderService.loadStores()).thenReturn(expectedStores);

        List<Store> result = storeService.loadStoresFromJson();

        verify(dataLoaderService).loadStores();
        assertThat(result).isEqualTo(expectedStores);
    }

    @Test
    void refreshStoresFromJson_shouldDeleteAllLoadAndAddAll() {
        List<Store> stores = List.of(StoreBuilder.store1(), StoreBuilder.store2());
        when(dataLoaderService.loadStores()).thenReturn(stores);
        when(storeRepository.saveAll(stores)).thenReturn(stores);

        List<Store> result = storeService.refreshStoresFromJson();

        verify(storeRepository).truncateAll();
        verify(entityManager).clear();
        verify(dataLoaderService).loadStores();
        verify(storeRepository).saveAll(stores);
        assertThat(result).isEqualTo(stores);
    }

    @Test
    void add_shouldSaveStore() {
        Store store = StoreBuilder.store1();

        storeService.add(store);

        verify(storeRepository).save(store);
    }

    @Test
    void addAll_shouldSaveAllStores() {
        List<Store> stores = List.of(StoreBuilder.store1(), StoreBuilder.store2());

        storeService.addAll(stores);

        verify(storeRepository).saveAll(stores);
    }

    @Test
    void getAllStores_shouldReturnAllStores() {
        List<Store> expectedStores = List.of(StoreBuilder.store1(), StoreBuilder.store2());
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        when(storeRepository.findAll(sort)).thenReturn(expectedStores);

        List<Store> result = storeService.getAllStores();

        verify(storeRepository).findAll(sort);
        assertThat(result).isEqualTo(expectedStores);
    }

    @Test
    void getStoreById_whenStoreExists_shouldReturnStore() {
        String storeId = "S1";
        Store expectedStore = StoreBuilder.store1();
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(expectedStore));

        Store result = storeService.getStoreById(storeId);

        verify(storeRepository).findById(storeId);
        assertThat(result).isEqualTo(expectedStore);
    }

    @Test
    void getStoreById_whenStoreNotFound_shouldThrowResourceNotFoundException() {
        String storeId = "S999";
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeService.getStoreById(storeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Store with id 'S999' not found");

        verify(storeRepository).findById(storeId);
    }

    @Test
    void deleteAll_shouldDeleteAllStores() {
        storeService.deleteAll();

        verify(storeRepository).truncateAll();
        verify(entityManager).clear();
    }
}