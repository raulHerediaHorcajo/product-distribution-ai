package com.productdistribution.backend.integration.repositories;

import com.productdistribution.backend.repositories.MetricsRepository;
import com.productdistribution.backend.repositories.StockAssignmentRepository;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.repositories.UnfulfilledDemandRepository;
import com.productdistribution.backend.repositories.WarehouseRepository;
import com.productdistribution.backend.utils.StockAssignmentBuilder;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.UnfulfilledDemandBuilder;
import com.productdistribution.backend.utils.WarehouseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MetricsRepositoryIntegrationTest {

    @Autowired
    private MetricsRepository metricsRepository;

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
    void getGlobalMetrics_shouldReturnGlobalMetrics() {
        MetricsRepository.GlobalMetricsProjection result = metricsRepository.getGlobalMetrics();

        assertThat(result).isNotNull();
        assertThat(result.getTotalShipments()).isEqualTo(2L);
        assertThat(result.getFulfilledUnits()).isEqualTo(92L);
        assertThat(result.getUnfulfilledUnits()).isEqualTo(35L);
        assertThat(result.getAverageDistance()).isEqualTo(125.0);
    }

    @Test
    void getDetailedMetrics_whenNoFilters_shouldReturnAllMetrics() {
        MetricsRepository.DetailedMetricsProjection result = metricsRepository.getDetailedMetrics(null, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getTotalShipments()).isEqualTo(2L);
        assertThat(result.getTotalFulfilled()).isEqualTo(92L);
        assertThat(result.getTotalUnfulfilled()).isEqualTo(35L);
        assertThat(result.getTotalDistance()).isEqualTo(250.0);
        assertThat(result.getAvgShipmentSize()).isEqualTo(46.0);
        assertThat(result.getUniqueProductsDistributed()).isEqualTo(2);
        assertThat(result.getUniqueProductsRequested()).isEqualTo(2);
        assertThat(result.getServedStores()).isEqualTo(2);
        assertThat(result.getFullyServedStores()).isZero();
        assertThat(result.getTotalStores()).isEqualTo(2);
    }

    @Test
    void getDetailedMetrics_whenWarehouseIdFilter_shouldReturnFilteredMetrics() {
        MetricsRepository.DetailedMetricsProjection result = metricsRepository.getDetailedMetrics("W1", null, null);

        assertThat(result).isNotNull();
        assertThat(result.getTotalShipments()).isEqualTo(2L);
        assertThat(result.getTotalFulfilled()).isEqualTo(92L);
        assertThat(result.getTotalUnfulfilled()).isEqualTo(35L);
        assertThat(result.getTotalDistance()).isEqualTo(250.0);
        assertThat(result.getAvgShipmentSize()).isEqualTo(46.0);
        assertThat(result.getUniqueProductsDistributed()).isEqualTo(2);
        assertThat(result.getUniqueProductsRequested()).isEqualTo(2);
        assertThat(result.getServedStores()).isEqualTo(2);
        assertThat(result.getFullyServedStores()).isZero();
        assertThat(result.getTotalStores()).isEqualTo(2);
    }

    @Test
    void getDetailedMetrics_whenStoreIdFilter_shouldReturnFilteredMetrics() {
        MetricsRepository.DetailedMetricsProjection result = metricsRepository.getDetailedMetrics(null, "S1", null);

        assertThat(result).isNotNull();
        assertThat(result.getTotalShipments()).isEqualTo(1L);
        assertThat(result.getTotalFulfilled()).isEqualTo(72L);
        assertThat(result.getTotalUnfulfilled()).isEqualTo(15L);
        assertThat(result.getTotalDistance()).isEqualTo(100.0);
        assertThat(result.getAvgShipmentSize()).isEqualTo(72.0);
        assertThat(result.getUniqueProductsDistributed()).isEqualTo(2);
        assertThat(result.getUniqueProductsRequested()).isEqualTo(2);
        assertThat(result.getServedStores()).isEqualTo(1);
        assertThat(result.getFullyServedStores()).isZero();
        assertThat(result.getTotalStores()).isEqualTo(1);
    }

    @Test
    void getDetailedMetrics_whenProductIdFilter_shouldReturnFilteredMetrics() {
        MetricsRepository.DetailedMetricsProjection result = metricsRepository.getDetailedMetrics(null, null, "P1");

        assertThat(result).isNotNull();
        assertThat(result.getTotalShipments()).isEqualTo(2L);
        assertThat(result.getTotalFulfilled()).isEqualTo(65L);
        assertThat(result.getTotalUnfulfilled()).isEqualTo(15L);
        assertThat(result.getTotalDistance()).isEqualTo(250.0);
        assertThat(result.getAvgShipmentSize()).isEqualTo(32.5);
        assertThat(result.getUniqueProductsDistributed()).isEqualTo(1);
        assertThat(result.getUniqueProductsRequested()).isEqualTo(1);
        assertThat(result.getServedStores()).isEqualTo(2);
        assertThat(result.getFullyServedStores()).isEqualTo(1);
        assertThat(result.getTotalStores()).isEqualTo(2);
    }

    @Test
    void getUnfulfilledDemand_whenNoFilters_shouldReturnAllUnfulfilledDemands() {
        MetricsRepository.UnfulfilledDemandProjection result = metricsRepository.getUnfulfilledDemand(null, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getTotalUnits()).isEqualTo(35L);
        assertThat(result.getUnitsByStockShortage()).isEqualTo(15L);
        assertThat(result.getUnitsByCapacityShortage()).isEqualTo(20L);
    }

    @Test
    void getUnfulfilledDemand_whenStoreIdFilter_shouldReturnFilteredUnfulfilledDemands() {
        MetricsRepository.UnfulfilledDemandProjection result = metricsRepository.getUnfulfilledDemand(null, "S1", null);

        assertThat(result).isNotNull();
        assertThat(result.getTotalUnits()).isEqualTo(15L);
        assertThat(result.getUnitsByStockShortage()).isEqualTo(15L);
        assertThat(result.getUnitsByCapacityShortage()).isZero();
    }

    @Test
    void getDistanceStats_whenNoFilters_shouldReturnAllDistanceStats() {
        MetricsRepository.DistanceStatsProjection result = metricsRepository.getDistanceStats(null, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getMinDistance()).isEqualTo(100.0);
        assertThat(result.getMaxDistance()).isEqualTo(150.0);
        assertThat(result.getMedianDistance()).isEqualTo(125.0);
    }

    @Test
    void getWarehouseMetrics_whenNoFilters_shouldReturnWarehouseMetrics() {
        List<MetricsRepository.WarehouseMetricsProjection> result = metricsRepository.getWarehouseMetrics(null, null, null);

        assertThat(result).isNotNull()
                .hasSize(1)
                .first()
                .satisfies(warehouse -> {
                    assertThat(warehouse.getWarehouseId()).isEqualTo("W1");
                    assertThat(warehouse.getTotalUnits()).isEqualTo(92L);
                    assertThat(warehouse.getPercentage()).isEqualTo(100.0);
                    assertThat(warehouse.getAvgDistance()).isEqualTo(125.0);
                });
    }

    @Test
    void getTopProducts_whenNoFilters_shouldReturnTopProducts() {
        List<MetricsRepository.TopProductProjection> result = metricsRepository.getTopProducts(null, null, null);

        assertThat(result).isNotNull()
                .hasSize(2)
                .satisfies(products -> {
                    assertThat(products.get(0).getProductId()).isEqualTo("P1");
                    assertThat(products.get(0).getTotalQuantity()).isEqualTo(65L);
                    assertThat(products.get(1).getProductId()).isEqualTo("P2");
                    assertThat(products.get(1).getTotalQuantity()).isEqualTo(27L);
                });
    }

    @Test
    void getCapacityUtilization_whenNoFilters_shouldReturnCapacityUtilization() {
        MetricsRepository.CapacityUtilizationProjection result = metricsRepository.getCapacityUtilization(null, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getUsedCapacity()).isEqualTo(20L);
        assertThat(result.getTotalCapacity()).isEqualTo(300L);
        assertThat(result.getTotalStores()).isEqualTo(2);
        assertThat(result.getStoresAtCapacity()).isZero();
    }
}