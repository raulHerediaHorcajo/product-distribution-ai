package com.productdistribution.backend.unit.services;

import com.productdistribution.backend.entities.StockAssignment;
import com.productdistribution.backend.repositories.StockAssignmentRepository;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.repositories.specification.StockAssignmentSpecifications;
import com.productdistribution.backend.services.StockAssignmentService;
import com.productdistribution.backend.utils.StockAssignmentBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockAssignmentServiceUnitTest {

    @Mock
    private StockAssignmentRepository stockAssignmentRepository;

    @InjectMocks
    private StockAssignmentService stockAssignmentService;

    @Test
    void add_shouldSaveStockAssignment() {
        StockAssignment stockAssignment = StockAssignmentBuilder.stockAssignment1();

        stockAssignmentService.add(stockAssignment);

        verify(stockAssignmentRepository).save(stockAssignment);
    }

    @Test
    void addAll_shouldSaveAllStockAssignments() {
        List<StockAssignment> stockAssignments = List.of(
                StockAssignmentBuilder.stockAssignment1(),
                StockAssignmentBuilder.stockAssignment2()
        );

        stockAssignmentService.addAll(stockAssignments);

        verify(stockAssignmentRepository).saveAll(stockAssignments);
    }

    @Test
    void getAllStockAssignments_shouldReturnAllStockAssignments() {
        List<StockAssignment> expectedAssignments = List.of(
                StockAssignmentBuilder.stockAssignment1(),
                StockAssignmentBuilder.stockAssignment2()
        );
        when(stockAssignmentRepository.findAll()).thenReturn(expectedAssignments);

        List<StockAssignment> result = stockAssignmentService.getAllStockAssignments();

        verify(stockAssignmentRepository).findAll();
        assertThat(result).isEqualTo(expectedAssignments);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAssignments_shouldReturnFilteredStockAssignments() {
        StockAssignmentCriteria criteria = new StockAssignmentCriteria();
        criteria.setStoreId("S1");
        Specification<StockAssignment> specification = mock(Specification.class);
        List<StockAssignment> expectedAssignments = List.of(StockAssignmentBuilder.stockAssignment1());
    
        try (MockedStatic<StockAssignmentSpecifications> mocked =
                        mockStatic(StockAssignmentSpecifications.class)) {
    
            mocked.when(() -> StockAssignmentSpecifications.withCriteria(criteria))
                    .thenReturn(specification);
            when(stockAssignmentRepository.findAll(specification))
                    .thenReturn(expectedAssignments);
    
            List<StockAssignment> result = stockAssignmentService.getAssignments(criteria);
    
            mocked.verify(() -> StockAssignmentSpecifications.withCriteria(criteria));
            verify(stockAssignmentRepository).findAll(specification);
            assertThat(result).isEqualTo(expectedAssignments);
        }
    }

    @Test
    void getByStoreId_shouldReturnStockAssignmentsForStore() {
        String storeId = "S1";
        List<StockAssignment> expectedAssignments = List.of(StockAssignmentBuilder.stockAssignment1());
        when(stockAssignmentRepository.findByStoreId(storeId)).thenReturn(expectedAssignments);

        List<StockAssignment> result = stockAssignmentService.getByStoreId(storeId);

        verify(stockAssignmentRepository).findByStoreId(storeId);
        assertThat(result).isEqualTo(expectedAssignments);
    }

    @Test
    void getByProductId_shouldReturnStockAssignmentsForProduct() {
        String productId = "P1";
        List<StockAssignment> expectedAssignments = List.of(StockAssignmentBuilder.stockAssignment1());
        when(stockAssignmentRepository.findByProductId(productId)).thenReturn(expectedAssignments);

        List<StockAssignment> result = stockAssignmentService.getByProductId(productId);

        verify(stockAssignmentRepository).findByProductId(productId);
        assertThat(result).isEqualTo(expectedAssignments);
    }

    @Test
    void getByWarehouseId_shouldReturnStockAssignmentsForWarehouse() {
        String warehouseId = "W1";
        List<StockAssignment> expectedAssignments = List.of(StockAssignmentBuilder.stockAssignment1());
        when(stockAssignmentRepository.findByWarehouseId(warehouseId)).thenReturn(expectedAssignments);

        List<StockAssignment> result = stockAssignmentService.getByWarehouseId(warehouseId);

        verify(stockAssignmentRepository).findByWarehouseId(warehouseId);
        assertThat(result).isEqualTo(expectedAssignments);
    }

    @Test
    void deleteAll_shouldDeleteAllStockAssignments() {
        stockAssignmentService.deleteAll();

        verify(stockAssignmentRepository).truncateAll();
    }
}