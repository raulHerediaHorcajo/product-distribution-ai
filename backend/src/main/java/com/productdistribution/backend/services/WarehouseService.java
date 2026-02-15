package com.productdistribution.backend.services;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.repositories.WarehouseRepository;

import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final DataLoaderService dataLoaderService;
    private final EntityManager entityManager;

    @Autowired
    public WarehouseService(WarehouseRepository warehouseRepository, DataLoaderService dataLoaderService, EntityManager entityManager) {
        this.warehouseRepository = warehouseRepository;
        this.dataLoaderService = dataLoaderService;
        this.entityManager = entityManager;
    }

    public List<Warehouse> loadWarehousesFromJson() {
        return dataLoaderService.loadWarehouses();
    }

    @Transactional
    public List<Warehouse> refreshWarehousesFromJson() {
        deleteAll();
        List<Warehouse> warehouses = loadWarehousesFromJson();
        addAll(warehouses);
        return warehouses;
    }

    public void add(Warehouse warehouse) {
        warehouseRepository.save(warehouse);
    }

    public void addAll(List<Warehouse> warehouses) {
        warehouseRepository.saveAll(warehouses);
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Warehouse getWarehouseById(String id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
    }

    public void deleteAll() {
        warehouseRepository.truncateAll();
        entityManager.clear();
    }
}