package com.productdistribution.backend.integration.services;

import com.productdistribution.backend.entities.StockAssignment;
import com.productdistribution.backend.repositories.StockAssignmentRepository;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.repositories.WarehouseRepository;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.services.StockAssignmentService;
import com.productdistribution.backend.utils.StockAssignmentBuilder;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.WarehouseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StockAssignmentServiceIntegrationTest {

    @Autowired
    private StockAssignmentService stockAssignmentService;

    @Autowired
    private StockAssignmentRepository stockAssignmentRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @BeforeEach
    void setUp() {
        storeRepository.saveAll(List.of(
            StoreBuilder.store1WithoutProductItemIds(),
            StoreBuilder.store2WithoutProductItemIds()
        ));
        warehouseRepository.saveAll(List.of(
            WarehouseBuilder.warehouse1WithoutProductItemIds(),
            WarehouseBuilder.warehouse2WithoutProductItemIds()
        ));
    }

    @Test
    void add_shouldSaveStockAssignment() {
        StockAssignment stockAssignment = StockAssignmentBuilder.stockAssignment1WithoutId();

        stockAssignmentService.add(stockAssignment);

        Optional<StockAssignment> found = stockAssignmentRepository.findById(stockAssignment.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(stockAssignment);
    }

    @Test
    void addAll_shouldSaveAllStockAssignments() {
        List<StockAssignment> stockAssignments = List.of(
            StockAssignmentBuilder.stockAssignment1WithoutId(),
            StockAssignmentBuilder.stockAssignment2WithoutId()
        );

        stockAssignmentService.addAll(stockAssignments);

        List<StockAssignment> found = stockAssignmentRepository.findAll();
        assertThat(found).usingRecursiveComparison().isEqualTo(stockAssignments);
    }

    @Test
    void getAllStockAssignments_shouldReturnAllStockAssignments() {
        List<StockAssignment> stockAssignments = List.of(
            StockAssignmentBuilder.stockAssignment1WithoutId(),
            StockAssignmentBuilder.stockAssignment2WithoutId()
        );
        stockAssignmentRepository.saveAll(stockAssignments);

        List<StockAssignment> result = stockAssignmentService.getAllStockAssignments();

        assertThat(result).usingRecursiveComparison().isEqualTo(stockAssignments);
    }

    @Test
    void getAllStockAssignments_whenNoStockAssignments_shouldReturnEmptyList() {
        List<StockAssignment> result = stockAssignmentService.getAllStockAssignments();

        assertThat(result).isEmpty();
    }

    @Test
    void getAssignments_whenAllCriteriaProvided_shouldReturnFilteredStockAssignments() {
        List<StockAssignment> stockAssignments = givenStockAssignments();

        StockAssignmentCriteria criteria = new StockAssignmentCriteria();
        criteria.setStoreId("S1");
        criteria.setWarehouseId("W1");
        criteria.setProductId("P1");

        List<StockAssignment> result = stockAssignmentService.getAssignments(criteria);

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(stockAssignments.get(0)));
    }

    @Test
    void getAssignments_whenNoCriteriaProvided_shouldReturnAllStockAssignments() {
        List<StockAssignment> stockAssignments = List.of(
            StockAssignmentBuilder.stockAssignment1WithoutId(),
            StockAssignmentBuilder.stockAssignment2WithoutId()
        );
        stockAssignmentRepository.saveAll(stockAssignments);

        StockAssignmentCriteria criteria = new StockAssignmentCriteria();

        List<StockAssignment> result = stockAssignmentService.getAssignments(criteria);

        assertThat(result).usingRecursiveComparison().isEqualTo(stockAssignments);
    }

    @Test
    void getByStoreId_shouldReturnStockAssignmentsForStore() {
        givenStockAssignments();

        List<StockAssignment> result = stockAssignmentService.getByStoreId("S1");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(StockAssignment::getStoreId)
                .containsExactly("S1", "S1");
    }

    @Test
    void getByProductId_shouldReturnStockAssignmentsForProduct() {
        givenStockAssignments();

        List<StockAssignment> result = stockAssignmentService.getByProductId("P1");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(StockAssignment::getProductId)
                .containsExactly("P1", "P1");
    }

    @Test
    void getByWarehouseId_shouldReturnStockAssignmentsForWarehouse() {
        givenStockAssignments();

        List<StockAssignment> result = stockAssignmentService.getByWarehouseId("W1");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(StockAssignment::getWarehouseId)
                .containsExactly("W1", "W1");
    }

    private List<StockAssignment> givenStockAssignments() {
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
    void deleteAll_shouldDeleteAllStockAssignments() {
        List<StockAssignment> stockAssignments = List.of(
            StockAssignmentBuilder.stockAssignment1WithoutId(),
            StockAssignmentBuilder.stockAssignment2WithoutId()
        );
        stockAssignmentRepository.saveAll(stockAssignments);

        stockAssignmentService.deleteAll();

        List<StockAssignment> found = stockAssignmentRepository.findAll();
        assertThat(found).isEmpty();
    }
}