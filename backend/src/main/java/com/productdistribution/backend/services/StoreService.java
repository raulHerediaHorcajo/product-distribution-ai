package com.productdistribution.backend.services;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.repositories.StoreRepository;

import java.util.List;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final DataLoaderService dataLoaderService;
    private final EntityManager entityManager;

    @Autowired
    public StoreService(StoreRepository storeRepository, DataLoaderService dataLoaderService, EntityManager entityManager) {
        this.storeRepository = storeRepository;
        this.dataLoaderService = dataLoaderService;
        this.entityManager = entityManager;
    }

    public List<Store> loadStoresFromJson() {
        return dataLoaderService.loadStores();
    }

    @Transactional
    public List<Store> refreshStoresFromJson() {
        deleteAll();
        List<Store> stores = loadStoresFromJson();
        addAll(stores);
        return stores;
    }

    public void add(Store store) {
        storeRepository.save(store);
    }

    public void addAll(List<Store> stores) {
        storeRepository.saveAll(stores);
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Store getStoreById(String id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", id));
    }

    public void deleteAll() {
        storeRepository.truncateAll();
        entityManager.clear();
    }
}