package com.productdistribution.backend.unit.services;

import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.repositories.WarehouseRepository;
import com.productdistribution.backend.services.DataLoaderService;
import com.productdistribution.backend.services.WarehouseService;
import com.productdistribution.backend.utils.WarehouseBuilder;

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
class WarehouseServiceUnitTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private DataLoaderService dataLoaderService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private WarehouseService warehouseService;

    @Test
    void loadWarehousesFromJson_shouldReturnWarehousesFromDataLoader() {
        List<Warehouse> expectedWarehouses = List.of(WarehouseBuilder.warehouse1(), WarehouseBuilder.warehouse2());
        when(dataLoaderService.loadWarehouses()).thenReturn(expectedWarehouses);

        List<Warehouse> result = warehouseService.loadWarehousesFromJson();

        verify(dataLoaderService).loadWarehouses();
        assertThat(result).isEqualTo(expectedWarehouses);
    }

    @Test
    void refreshWarehousesFromJson_shouldDeleteAllLoadAndAddAll() {
        List<Warehouse> warehouses = List.of(WarehouseBuilder.warehouse1(), WarehouseBuilder.warehouse2());
        when(dataLoaderService.loadWarehouses()).thenReturn(warehouses);
        when(warehouseRepository.saveAll(warehouses)).thenReturn(warehouses);

        List<Warehouse> result = warehouseService.refreshWarehousesFromJson();

        verify(warehouseRepository).truncateAll();
        verify(entityManager).clear();
        verify(dataLoaderService).loadWarehouses();
        verify(warehouseRepository).saveAll(warehouses);
        assertThat(result).isEqualTo(warehouses);
    }

    @Test
    void add_shouldSaveWarehouse() {
        Warehouse warehouse = WarehouseBuilder.warehouse1();

        warehouseService.add(warehouse);

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void addAll_shouldSaveAllWarehouses() {
        List<Warehouse> warehouses = List.of(WarehouseBuilder.warehouse1(), WarehouseBuilder.warehouse2());

        warehouseService.addAll(warehouses);

        verify(warehouseRepository).saveAll(warehouses);
    }

    @Test
    void getAllWarehouses_shouldReturnAllWarehouses() {
        List<Warehouse> expectedWarehouses = List.of(WarehouseBuilder.warehouse1(), WarehouseBuilder.warehouse2());
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        when(warehouseRepository.findAll(sort)).thenReturn(expectedWarehouses);

        List<Warehouse> result = warehouseService.getAllWarehouses();

        verify(warehouseRepository).findAll(sort);
        assertThat(result).isEqualTo(expectedWarehouses);
    }

    @Test
    void getWarehouseById_whenWarehouseExists_shouldReturnWarehouse() {
        String warehouseId = "W1";
        Warehouse expectedWarehouse = WarehouseBuilder.warehouse1();
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(expectedWarehouse));

        Warehouse result = warehouseService.getWarehouseById(warehouseId);

        verify(warehouseRepository).findById(warehouseId);
        assertThat(result).isEqualTo(expectedWarehouse);
    }

    @Test
    void getWarehouseById_whenWarehouseNotFound_shouldThrowResourceNotFoundException() {
        String warehouseId = "W999";
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.getWarehouseById(warehouseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Warehouse with id 'W999' not found");

        verify(warehouseRepository).findById(warehouseId);
    }

    @Test
    void deleteAll_shouldDeleteAllWarehouses() {
        warehouseService.deleteAll();

        verify(warehouseRepository).truncateAll();
        verify(entityManager).clear();
    }
}