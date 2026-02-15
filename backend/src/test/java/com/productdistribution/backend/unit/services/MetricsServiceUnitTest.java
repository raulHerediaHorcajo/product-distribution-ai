package com.productdistribution.backend.unit.services;

import com.productdistribution.backend.dtos.metrics.*;
import com.productdistribution.backend.mappers.MetricsMapper;
import com.productdistribution.backend.repositories.MetricsRepository;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.services.MetricsService;
import com.productdistribution.backend.utils.metrics.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceUnitTest {

    @Mock
    private MetricsRepository metricsRepository;

    @Mock
    private MetricsMapper metricsMapper;

    @InjectMocks
    private MetricsService metricsService;

    @Test
    void calculateGlobalMetrics_shouldReturnGlobalMetricsDTO() {
        MetricsRepository.GlobalMetricsProjection projection = mock(MetricsRepository.GlobalMetricsProjection.class);
        GlobalMetricsDTO expectedGlobalMetrics = GlobalMetricsDTOBuilder.globalMetrics1();
        when(metricsRepository.getGlobalMetrics()).thenReturn(projection);
        when(metricsMapper.toGlobalMetricsDTO(projection)).thenReturn(expectedGlobalMetrics);

        GlobalMetricsDTO result = metricsService.calculateGlobalMetrics();

        verify(metricsRepository).getGlobalMetrics();
        verify(metricsMapper).toGlobalMetricsDTO(projection);
        assertThat(result).isEqualTo(expectedGlobalMetrics);
    }

    @Test
    void calculateGlobalMetrics_whenException_shouldReturnEmptyGlobalMetrics() {
        GlobalMetricsDTO expectedGlobalMetrics = GlobalMetricsDTOBuilder.defaultGlobalMetrics();
        when(metricsRepository.getGlobalMetrics()).thenThrow(new RuntimeException("Database error"));

        GlobalMetricsDTO result = metricsService.calculateGlobalMetrics();

        verify(metricsRepository).getGlobalMetrics();
        verify(metricsMapper, never()).toGlobalMetricsDTO(any(MetricsRepository.GlobalMetricsProjection.class));
        assertThat(result).isEqualTo(expectedGlobalMetrics);
    }

    @Test
    void calculateDetailedMetrics_shouldReturnDetailedMetricsDTO() {
        DetailedMetricsDTO expectedDetailedMetrics = DetailedMetricsDTOBuilder.detailedMetrics1();
        StockAssignmentCriteria criteria = createCriteria("W1", "S1", "P1");
        MetricsRepository.DetailedMetricsProjection basicMetrics = mockBasicMetrics(expectedDetailedMetrics);
        when(basicMetrics.getTotalUnfulfilled()).thenReturn(52L);
        setupCommonMocks(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId(), basicMetrics, expectedDetailedMetrics);

        DetailedMetricsDTO result = metricsService.calculateDetailedMetrics(criteria);

        verify(metricsRepository).getDetailedMetrics(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId());
        verify(basicMetrics, times(3)).getTotalFulfilled();
        verify(basicMetrics).getTotalUnfulfilled();
        verify(metricsMapper).toStoresServedDTO(basicMetrics);
        verify(metricsRepository).getUnfulfilledDemand(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId());
        verify(metricsMapper).toUnfulfilledDemandDTO(any(MetricsRepository.UnfulfilledDemandProjection.class));
        verify(metricsRepository).getDistanceStats(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId());
        verify(metricsMapper).toDistanceStatsDTO(any(MetricsRepository.DistanceStatsProjection.class));
        verify(metricsRepository).getCapacityUtilization(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId());
        verify(metricsMapper).toCapacityUtilizationDTO(any(MetricsRepository.CapacityUtilizationProjection.class));
        verify(metricsRepository).getWarehouseMetrics(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId());
        verify(metricsMapper).toWarehouseUnitsDTOList(anyList());
        verify(metricsRepository).getTopProducts(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId());
        verify(metricsMapper).toTopProductDTOList(anyList());
        verify(basicMetrics).getTotalDistance();
        verify(basicMetrics).getTotalShipments();
        verify(basicMetrics).getAvgShipmentSize();
        verify(basicMetrics).getUniqueProductsDistributed();
        verify(basicMetrics).getUniqueProductsRequested();

        assertThat(result).usingRecursiveComparison()
                .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1, "fulfillmentRate")
                .isEqualTo(expectedDetailedMetrics);
    }

    @Test
    void calculateDetailedMetrics_whenException_shouldReturnEmptyDetailedMetrics() {
        StockAssignmentCriteria criteria = new StockAssignmentCriteria();
        DetailedMetricsDTO expectedDetailedMetrics = DetailedMetricsDTOBuilder.defaultDetailedMetrics();
        when(metricsRepository.getDetailedMetrics(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId()))
                .thenThrow(new RuntimeException("Database error"));

        DetailedMetricsDTO result = metricsService.calculateDetailedMetrics(criteria);

        verify(metricsRepository).getDetailedMetrics(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId());
        verify(metricsMapper, never()).toStoresServedDTO(any(MetricsRepository.DetailedMetricsProjection.class));
        assertThat(result).isEqualTo(expectedDetailedMetrics);
    }

    @Test
    void calculateDetailedMetrics_whenTotalDemandedIsZero_shouldReturnZeroFulfillmentRate() {
        StockAssignmentCriteria criteria = createCriteria("W1", "S1", "P1");
        DetailedMetricsDTO defaultMetrics = DetailedMetricsDTOBuilder.defaultDetailedMetrics();
        MetricsRepository.DetailedMetricsProjection basicMetrics = mockBasicMetrics(defaultMetrics);
        when(basicMetrics.getTotalFulfilled()).thenReturn(0L);
        when(basicMetrics.getTotalUnfulfilled()).thenReturn(0L);
        setupCommonMocks(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId(), basicMetrics, defaultMetrics);

        DetailedMetricsDTO result = metricsService.calculateDetailedMetrics(criteria);

        verify(basicMetrics, times(3)).getTotalFulfilled();
        verify(basicMetrics).getTotalUnfulfilled();
        assertThat(result.fulfillmentRate()).isZero();
    }

    @ParameterizedTest
    @MethodSource("unitsByWarehouseTestCases")
    void calculateDetailedMetrics_whenUnitsByWarehouseIsNullOrEmptyOrTotalUnitsIsZero_shouldReturnZeroWeightedAverageDistance(
            List<WarehouseUnitsDTO> unitsByWarehouse, String testCase) {
        StockAssignmentCriteria criteria = createCriteria("W1", "S1", "P1");
        DetailedMetricsDTO expectedMetrics = DetailedMetricsDTOBuilder.detailedMetrics1();
        MetricsRepository.DetailedMetricsProjection basicMetrics = mockBasicMetrics(expectedMetrics);
        when(basicMetrics.getTotalUnfulfilled()).thenReturn(52L);
        setupCommonMocks(criteria.getWarehouseId(), criteria.getStoreId(), criteria.getProductId(), basicMetrics, expectedMetrics);
        when(metricsMapper.toWarehouseUnitsDTOList(anyList())).thenReturn(unitsByWarehouse);

        DetailedMetricsDTO result = metricsService.calculateDetailedMetrics(criteria);

        assertThat(result.efficiencyScore()).isNotNull().isGreaterThanOrEqualTo(20);
    }

    private static Stream<Arguments> unitsByWarehouseTestCases() {
        return Stream.of(
                Arguments.of(null, "null"),
                Arguments.of(List.of(), "empty"),
                Arguments.of(List.of(
                        WarehouseUnitsDTOBuilder.builder()
                                .withWarehouseId("W1")
                                .withTotalUnits(0L)
                                .withPercentage(0.0)
                                .withAvgDistance(1500.0)
                                .build()
                ), "zeroUnits")
        );
    }

    private StockAssignmentCriteria createCriteria(String warehouseId, String storeId, String productId) {
        StockAssignmentCriteria criteria = new StockAssignmentCriteria();
        criteria.setWarehouseId(warehouseId);
        criteria.setStoreId(storeId);
        criteria.setProductId(productId);
        return criteria;
    }

    private MetricsRepository.DetailedMetricsProjection mockBasicMetrics(DetailedMetricsDTO metrics) {
        MetricsRepository.DetailedMetricsProjection basicMetrics = mock(MetricsRepository.DetailedMetricsProjection.class);
        when(basicMetrics.getTotalShipments()).thenReturn(metrics.totalShipments());
        when(basicMetrics.getTotalFulfilled()).thenReturn(metrics.fulfilledUnits());
        when(basicMetrics.getTotalDistance()).thenReturn(metrics.totalDistance());
        when(basicMetrics.getAvgShipmentSize()).thenReturn(metrics.avgShipmentSize());
        when(basicMetrics.getUniqueProductsDistributed()).thenReturn(metrics.uniqueProductsDistributed());
        when(basicMetrics.getUniqueProductsRequested()).thenReturn(metrics.uniqueProductsRequested());
        return basicMetrics;
    }

    private void setupCommonMocks(String warehouseId, String storeId, String productId,
                                   MetricsRepository.DetailedMetricsProjection basicMetrics,
                                   DetailedMetricsDTO expectedMetrics) {
        MetricsRepository.UnfulfilledDemandProjection unfulfilledProjection = mock(MetricsRepository.UnfulfilledDemandProjection.class);
        MetricsRepository.DistanceStatsProjection distanceProjection = mock(MetricsRepository.DistanceStatsProjection.class);
        MetricsRepository.CapacityUtilizationProjection capacityProjection = mock(MetricsRepository.CapacityUtilizationProjection.class);
        MetricsRepository.WarehouseMetricsProjection warehouseProjection = mock(MetricsRepository.WarehouseMetricsProjection.class);
        MetricsRepository.TopProductProjection topProductProjection = mock(MetricsRepository.TopProductProjection.class);

        when(metricsRepository.getDetailedMetrics(warehouseId, storeId, productId)).thenReturn(basicMetrics);
        when(metricsMapper.toStoresServedDTO(basicMetrics)).thenReturn(expectedMetrics.storesServed());
        when(metricsRepository.getUnfulfilledDemand(warehouseId, storeId, productId)).thenReturn(unfulfilledProjection);
        when(metricsMapper.toUnfulfilledDemandDTO(unfulfilledProjection)).thenReturn(expectedMetrics.unfulfilledDemand());
        when(metricsRepository.getDistanceStats(warehouseId, storeId, productId)).thenReturn(distanceProjection);
        when(metricsMapper.toDistanceStatsDTO(distanceProjection)).thenReturn(expectedMetrics.distanceStats());
        when(metricsRepository.getCapacityUtilization(warehouseId, storeId, productId)).thenReturn(capacityProjection);
        when(metricsMapper.toCapacityUtilizationDTO(capacityProjection)).thenReturn(expectedMetrics.capacityUtilization());
        when(metricsRepository.getWarehouseMetrics(warehouseId, storeId, productId)).thenReturn(List.of(warehouseProjection));
        when(metricsMapper.toWarehouseUnitsDTOList(anyList())).thenReturn(expectedMetrics.unitsByWarehouse());
        when(metricsRepository.getTopProducts(warehouseId, storeId, productId)).thenReturn(List.of(topProductProjection));
        when(metricsMapper.toTopProductDTOList(anyList())).thenReturn(expectedMetrics.topProducts());
    }
}