package com.productdistribution.backend.integration.repositories;

import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.repositories.WarehouseRepository;
import com.productdistribution.backend.utils.ProductItemBuilder;
import com.productdistribution.backend.utils.WarehouseBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class WarehouseRepositoryIntegrationTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Test
    void save_shouldPersistWarehouse() {
        Warehouse warehouse = WarehouseBuilder.warehouse1WithoutProductItemIds();

        Warehouse saved = warehouseRepository.save(warehouse);

        Optional<Warehouse> found = warehouseRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison()
            .ignoringFields("stock.id").isEqualTo(warehouse);
    }

    @Test
    void save_shouldPersistStockList() {
        Warehouse warehouse = WarehouseBuilder.builder()
                .withStock(List.of(ProductItemBuilder.productItem1WithoutId())).build();

        Warehouse saved = warehouseRepository.save(warehouse);

        Optional<Warehouse> found = warehouseRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStock()).hasSize(1);
        assertThat(found.get().getStock())
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
            .containsExactly(ProductItemBuilder.productItem1WithoutId());
    }

    @Test
    void save_whenStockIsNull_shouldPersistWarehouseWithNullStock() {
        Warehouse warehouse = WarehouseBuilder.builder()
                .withStock(null).build();

        Warehouse saved = warehouseRepository.save(warehouse);

        Optional<Warehouse> found = warehouseRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStock()).isNull();
    }

    @Test
    void findById_whenWarehouseExists_shouldReturnWarehouse() {
        Warehouse warehouse = WarehouseBuilder.warehouse2WithoutProductItemIds();
        Warehouse saved = warehouseRepository.save(warehouse);

        Optional<Warehouse> found = warehouseRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison()
            .ignoringFields("stock.id").isEqualTo(warehouse);
    }

    @Test
    void findById_whenWarehouseNotExists_shouldReturnEmpty() {
        Optional<Warehouse> found = warehouseRepository.findById("NON_EXISTENT");

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_whenWarehousesExist_shouldReturnAllWarehouses() {
        List<Warehouse> warehouses = List.of(
            WarehouseBuilder.warehouse1WithoutProductItemIds(),
            WarehouseBuilder.warehouse2WithoutProductItemIds()
        );
        warehouseRepository.saveAll(warehouses);

        List<Warehouse> found = warehouseRepository.findAll();

        assertThat(found).usingRecursiveComparison()
            .ignoringFields("stock.id").isEqualTo(warehouses);
    }

    @Test
    void findAllByOrderByIdAsc_whenWarehousesAreUnordered_shouldReturnAllSorted() {
        List<Warehouse> warehouses = List.of(WarehouseBuilder.warehouse2WithoutProductItemIds(), WarehouseBuilder.warehouse1WithoutProductItemIds());
        warehouseRepository.saveAll(warehouses);
        List<Warehouse> expectedWarehouses = List.of(WarehouseBuilder.warehouse1WithoutProductItemIds(), WarehouseBuilder.warehouse2WithoutProductItemIds());

        List<Warehouse> found = warehouseRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        
        assertThat(found).usingRecursiveComparison()
            .ignoringFields("stock.id").isEqualTo(expectedWarehouses);
    }

    @Test
    void findAll_whenNoWarehouses_shouldReturnEmptyList() {
        List<Warehouse> found = warehouseRepository.findAll();

        assertThat(found).isEmpty();
    }

    @Test
    void saveAll_shouldPersistMultipleWarehouses() {
        List<Warehouse> warehouses = List.of(
            WarehouseBuilder.warehouse2WithoutProductItemIds(),
            WarehouseBuilder.warehouse1WithoutProductItemIds()
        );

        warehouseRepository.saveAll(warehouses);

        List<Warehouse> found = warehouseRepository.findAll();
        assertThat(found).usingRecursiveComparison()
            .ignoringFields("stock.id").isEqualTo(warehouses);
    }

    @Test
    void truncateAll_shouldRemoveAllWarehouses() {
        List<Warehouse> warehouses = List.of(
            WarehouseBuilder.warehouse1WithoutProductItemIds(),
            WarehouseBuilder.warehouse2WithoutProductItemIds()
        );
        warehouseRepository.saveAll(warehouses);

        warehouseRepository.truncateAll();

        List<Warehouse> found = warehouseRepository.findAll();
        assertThat(found).isEmpty();
    }
}