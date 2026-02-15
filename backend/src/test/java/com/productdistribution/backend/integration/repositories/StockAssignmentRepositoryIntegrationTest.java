package com.productdistribution.backend.integration.repositories;

import com.productdistribution.backend.entities.StockAssignment;
import com.productdistribution.backend.repositories.StockAssignmentRepository;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.repositories.specification.StockAssignmentSpecifications;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.repositories.WarehouseRepository;
import com.productdistribution.backend.utils.StockAssignmentBuilder;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.WarehouseBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StockAssignmentRepositoryIntegrationTest {

    @Autowired
    private StockAssignmentRepository stockAssignmentRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Test
    void save_shouldPersistStockAssignment() {
        storeRepository.save(StoreBuilder.store1WithoutProductItemIds());
        warehouseRepository.save(WarehouseBuilder.warehouse1WithoutProductItemIds());

        StockAssignment stockAssignment = StockAssignmentBuilder.stockAssignment1WithoutId();

        StockAssignment saved = stockAssignmentRepository.saveAndFlush(stockAssignment);

        Optional<StockAssignment> found = stockAssignmentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(stockAssignment);
    }

    @Test
    void findById_whenStockAssignmentExists_shouldReturnStockAssignment() {
        storeRepository.save(StoreBuilder.store1WithoutProductItemIds());
        warehouseRepository.save(WarehouseBuilder.warehouse1WithoutProductItemIds());

        StockAssignment stockAssignment = StockAssignmentBuilder.stockAssignment2WithoutId();
        
        StockAssignment saved = stockAssignmentRepository.saveAndFlush(stockAssignment);

        Optional<StockAssignment> found = stockAssignmentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(stockAssignment);
    }

    @Test
    void findById_whenStockAssignmentNotExists_shouldReturnEmpty() {
        Optional<StockAssignment> found = stockAssignmentRepository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_whenStockAssignmentsExist_shouldReturnAllStockAssignments() {
        storeRepository.save(StoreBuilder.store1WithoutProductItemIds());
        warehouseRepository.save(WarehouseBuilder.warehouse1WithoutProductItemIds());
        List<StockAssignment> stockAssignments = List.of(
            StockAssignmentBuilder.stockAssignment1WithoutId(),
            StockAssignmentBuilder.stockAssignment2WithoutId()
        );
        stockAssignmentRepository.saveAll(stockAssignments);

        List<StockAssignment> found = stockAssignmentRepository.findAll();

        assertThat(found).usingRecursiveComparison().isEqualTo(stockAssignments);
    }

    @Test
    void findAll_whenNoStockAssignments_shouldReturnEmptyList() {
        List<StockAssignment> found = stockAssignmentRepository.findAll();

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_whenSpecificationProvided_shouldReturnFilteredStockAssignments() {
        List<StockAssignment> stockAssignments = givenStoresWarehousesAndStockAssignments();

        StockAssignmentCriteria criteria = new StockAssignmentCriteria();
        criteria.setStoreId("S1");
        criteria.setWarehouseId("W1");
        criteria.setProductId("P1");
        Specification<StockAssignment> specification = StockAssignmentSpecifications.withCriteria(criteria);

        List<StockAssignment> found = stockAssignmentRepository.findAll(specification);

        assertThat(found).usingRecursiveComparison().isEqualTo(List.of(stockAssignments.get(0)));
    }

    @Test
    void findByStoreId_shouldReturnStockAssignmentsForStore() {
        givenStoresWarehousesAndStockAssignments();

        List<StockAssignment> found = stockAssignmentRepository.findByStoreId("S1");

        assertThat(found).hasSize(2);
        assertThat(found).extracting(StockAssignment::getStoreId)
                .containsExactly("S1", "S1");
    }

    @Test
    void findByProductId_shouldReturnStockAssignmentsForProduct() {
        givenStoresWarehousesAndStockAssignments();

        List<StockAssignment> found = stockAssignmentRepository.findByProductId("P1");

        assertThat(found).hasSize(2);
        assertThat(found).extracting(StockAssignment::getProductId)
                .containsExactly("P1", "P1");
    }

    @Test
    void findByWarehouseId_shouldReturnStockAssignmentsForWarehouse() {
        givenStoresWarehousesAndStockAssignments();

        List<StockAssignment> found = stockAssignmentRepository.findByWarehouseId("W1");

        assertThat(found).hasSize(2);
        assertThat(found).extracting(StockAssignment::getWarehouseId)
                .containsExactly("W1", "W1");
    }

    private List<StockAssignment> givenStoresWarehousesAndStockAssignments() {
        storeRepository.saveAll(List.of(StoreBuilder.store1WithoutProductItemIds(),
                StoreBuilder.store2WithoutProductItemIds()));
        warehouseRepository.saveAll(List.of(WarehouseBuilder.warehouse1WithoutProductItemIds(),
                WarehouseBuilder.warehouse2WithoutProductItemIds()));
        StockAssignment assignment1 = StockAssignmentBuilder.builder().withStoreId("S1")
                .withWarehouseId("W1").withProductId("P1").build();
        StockAssignment assignment2 = StockAssignmentBuilder.builder().withStoreId("S1")
                .withWarehouseId("W2").withProductId("P2").build();
        StockAssignment assignment3 = StockAssignmentBuilder.builder().withStoreId("S2")
                .withWarehouseId("W1").withProductId("P1").build();
        List<StockAssignment> stockAssignments = List.of(assignment1, assignment2, assignment3);
        stockAssignmentRepository.saveAll(stockAssignments);
        return stockAssignments;
    }

    @Test
    void saveAll_shouldPersistMultipleStockAssignments() {
        storeRepository.save(StoreBuilder.store1WithoutProductItemIds());
        warehouseRepository.save(WarehouseBuilder.warehouse1WithoutProductItemIds());

        List<StockAssignment> stockAssignments = List.of(
            StockAssignmentBuilder.stockAssignment2WithoutId(),
            StockAssignmentBuilder.stockAssignment1WithoutId()
        );

        stockAssignmentRepository.saveAll(stockAssignments);

        List<StockAssignment> found = stockAssignmentRepository.findAll();
        assertThat(found).usingRecursiveComparison().isEqualTo(stockAssignments);
    }

    @Test
    void truncateAll_shouldRemoveAllStockAssignments() {
        storeRepository.save(StoreBuilder.store1WithoutProductItemIds());
        warehouseRepository.save(WarehouseBuilder.warehouse1WithoutProductItemIds());
        List<StockAssignment> stockAssignments = List.of(
            StockAssignmentBuilder.stockAssignment1WithoutId(),
            StockAssignmentBuilder.stockAssignment2WithoutId()
        );
        stockAssignmentRepository.saveAll(stockAssignments);

        stockAssignmentRepository.truncateAll();

        List<StockAssignment> found = stockAssignmentRepository.findAll();
        assertThat(found).isEmpty();
    }
}