package com.productdistribution.backend.integration.services;

import com.productdistribution.backend.dtos.metrics.DetailedMetricsDTO;
import com.productdistribution.backend.dtos.metrics.GlobalMetricsDTO;
import com.productdistribution.backend.repositories.StockAssignmentRepository;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.repositories.UnfulfilledDemandRepository;
import com.productdistribution.backend.repositories.WarehouseRepository;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.services.MetricsService;
import com.productdistribution.backend.utils.StockAssignmentBuilder;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.UnfulfilledDemandBuilder;
import com.productdistribution.backend.utils.WarehouseBuilder;
import com.productdistribution.backend.utils.metrics.DetailedMetricsDTOBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MetricsServiceIntegrationTest {

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private StockAssignmentRepository stockAssignmentRepository;

    @Autowired
    private UnfulfilledDemandRepository unfulfilledDemandRepository;

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

        stockAssignmentRepository.saveAll(List.of(
            StockAssignmentBuilder.stockAssignment1WithoutId(),
            StockAssignmentBuilder.stockAssignment2WithoutId(),
            StockAssignmentBuilder.stockAssignment3WithoutId()
        ));

        unfulfilledDemandRepository.saveAll(List.of(
            UnfulfilledDemandBuilder.unfulfilledDemand1WithoutId(),
            UnfulfilledDemandBuilder.unfulfilledDemand2WithoutId()
        ));
    }

    @Test
    void calculateGlobalMetrics_shouldReturnGlobalMetricsDTO() {
        GlobalMetricsDTO result = metricsService.calculateGlobalMetrics();

        assertThat(result).isNotNull();
        assertThat(result.totalShipments()).isEqualTo(2L);
        assertThat(result.fulfilledUnits()).isEqualTo(92L);
        assertThat(result.unfulfilledUnits()).isEqualTo(35L);
        assertThat(result.averageDistance()).isEqualTo(125.0);
    }

    @Test
    void calculateDetailedMetrics_whenNoFilters_shouldReturnDetailedMetricsDTO() {
        DetailedMetricsDTO expected = DetailedMetricsDTOBuilder.detailedMetricsWhenNoFilters();
        StockAssignmentCriteria criteria = new StockAssignmentCriteria();

        DetailedMetricsDTO result = metricsService.calculateDetailedMetrics(criteria);

        assertThat(result).usingRecursiveComparison()
            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1,
             "fulfillmentRate", "capacityUtilization.percentage")
            .isEqualTo(expected);
    }

    @Test
    void calculateDetailedMetrics_whenAllFilters_shouldReturnFilteredDetailedMetrics() {
        StockAssignmentCriteria criteria = new StockAssignmentCriteria();
        criteria.setWarehouseId("W1");
        criteria.setStoreId("S1");
        criteria.setProductId("P1");

        DetailedMetricsDTO result = metricsService.calculateDetailedMetrics(criteria);

        assertThat(result).isNotNull();
        assertThat(result.efficiencyScore()).isEqualTo(72);
        assertThat(result.totalShipments()).isEqualTo(1L);
        assertThat(result.storesServed().servedStores()).isEqualTo(1);
        assertThat(result.uniqueProductsDistributed()).isEqualTo(1);
        assertThat(result.unfulfilledDemand().totalUnits()).isEqualTo(15L);
        assertThat(result.fulfilledUnits()).isEqualTo(45L);
    }
}