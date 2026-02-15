package com.productdistribution.backend.integration.services;

import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.repositories.WarehouseRepository;
import com.productdistribution.backend.services.DataLoaderService;
import com.productdistribution.backend.services.WarehouseService;
import com.productdistribution.backend.utils.WarehouseBuilder;
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
class WarehouseServiceIntegrationTest {

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @MockitoBean
    private DataLoaderService dataLoaderService;

    @Test
    void loadWarehousesFromJson_shouldReturnWarehousesFromDataLoader() {
        List<Warehouse> warehouses = List.of(WarehouseBuilder.warehouse1WithoutProductItemIds(), WarehouseBuilder.warehouse2WithoutProductItemIds());
        when(dataLoaderService.loadWarehouses()).thenReturn(warehouses);

        List<Warehouse> result = warehouseService.loadWarehousesFromJson();

        verify(dataLoaderService).loadWarehouses();
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("stock.id").isEqualTo(warehouses);
    }

    @Test
    void refreshWarehousesFromJson_shouldDeleteAllAndLoadNewWarehouses() {
        Warehouse existingWarehouse = WarehouseBuilder.warehouse1WithoutProductItemIds();
        warehouseRepository.save(existingWarehouse);

        List<Warehouse> newWarehouses = List.of(WarehouseBuilder.warehouse2WithoutProductItemIds());
        when(dataLoaderService.loadWarehouses()).thenReturn(newWarehouses);

        List<Warehouse> result = warehouseService.refreshWarehousesFromJson();

        verify(dataLoaderService).loadWarehouses();
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("stock.id").isEqualTo(newWarehouses);
        List<Warehouse> found = warehouseRepository.findAll();
        assertThat(found).usingRecursiveComparison()
                .ignoringFields("stock.id").isEqualTo(newWarehouses);
        Optional<Warehouse> foundExistingWarehouse = warehouseRepository.findById(existingWarehouse.getId());
        assertThat(foundExistingWarehouse).isEmpty();
    }

    @Test
    void add_shouldSaveWarehouse() {
        Warehouse warehouse = WarehouseBuilder.warehouse1WithoutProductItemIds();

        warehouseService.add(warehouse);

        Optional<Warehouse> found = warehouseRepository.findById(warehouse.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison()
                .ignoringFields("stock.id").isEqualTo(warehouse);
    }

    @Test
    void addAll_shouldSaveAllWarehouses() {
        List<Warehouse> warehouses = List.of(WarehouseBuilder.warehouse1WithoutProductItemIds(), WarehouseBuilder.warehouse2WithoutProductItemIds());

        warehouseService.addAll(warehouses);

        List<Warehouse> found = warehouseRepository.findAll();
        assertThat(found).usingRecursiveComparison()
                .ignoringFields("stock.id").isEqualTo(warehouses);
    }

    @Test
    void getAllWarehouses_shouldReturnAllWarehouses() {
        List<Warehouse> warehouses = List.of(WarehouseBuilder.warehouse1WithoutProductItemIds(), WarehouseBuilder.warehouse2WithoutProductItemIds());
        warehouseRepository.saveAll(warehouses);

        List<Warehouse> result = warehouseService.getAllWarehouses();

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("stock.id").isEqualTo(warehouses);
    }

    @Test
    void getAllWarehouses_whenWarehousesAreUnordered_shouldReturnAllSorted() {
        List<Warehouse> warehouses = List.of(WarehouseBuilder.warehouse2WithoutProductItemIds(), WarehouseBuilder.warehouse1WithoutProductItemIds());
        warehouseRepository.saveAll(warehouses);
        List<Warehouse> expectedWarehouses = List.of(WarehouseBuilder.warehouse1WithoutProductItemIds(), WarehouseBuilder.warehouse2WithoutProductItemIds());

        List<Warehouse> result = warehouseService.getAllWarehouses();
        
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("stock.id").isEqualTo(expectedWarehouses);
    }

    @Test
    void getAllWarehouses_whenNoWarehouses_shouldReturnEmptyList() {
        List<Warehouse> result = warehouseService.getAllWarehouses();

        assertThat(result).isEmpty();
    }

    @Test
    void getWarehouseById_whenWarehouseExists_shouldReturnWarehouse() {
        Warehouse warehouse = WarehouseBuilder.warehouse1WithoutProductItemIds();
        warehouseRepository.save(warehouse);

        Warehouse result = warehouseService.getWarehouseById(warehouse.getId());

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("stock.id").isEqualTo(warehouse);
    }

    @Test
    void getWarehouseById_whenWarehouseNotFound_shouldThrowResourceNotFoundException() {
        assertThatThrownBy(() -> warehouseService.getWarehouseById("NON_EXISTENT"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Warehouse")
                .hasMessageContaining("NON_EXISTENT");
    }

    @Test
    void deleteAll_shouldDeleteAllWarehouses() {
        List<Warehouse> warehouses = List.of(WarehouseBuilder.warehouse1WithoutProductItemIds(), WarehouseBuilder.warehouse2WithoutProductItemIds());
        warehouseRepository.saveAll(warehouses);

        warehouseService.deleteAll();

        List<Warehouse> found = warehouseRepository.findAll();
        assertThat(found).isEmpty();
    }
}