package com.productdistribution.backend.unit.mappers;

import com.productdistribution.backend.dtos.metrics.*;
import com.productdistribution.backend.mappers.MetricsMapper;
import com.productdistribution.backend.repositories.MetricsRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MetricsMapperUnitTest {

    private final MetricsMapper metricsMapper = Mappers.getMapper(MetricsMapper.class);

    @Test
    void toGlobalMetricsDTO_shouldReturnDTO() {
        MetricsRepository.GlobalMetricsProjection projection = mock(MetricsRepository.GlobalMetricsProjection.class);
        when(projection.getTotalShipments()).thenReturn(100L);
        when(projection.getFulfilledUnits()).thenReturn(500L);
        when(projection.getUnfulfilledUnits()).thenReturn(50L);
        when(projection.getAverageDistance()).thenReturn(75.5);

        GlobalMetricsDTO dto = metricsMapper.toGlobalMetricsDTO(projection);

        assertThat(dto).isNotNull();
        assertThat(dto.totalShipments()).isEqualTo(100L);
        assertThat(dto.fulfilledUnits()).isEqualTo(500L);
        assertThat(dto.unfulfilledUnits()).isEqualTo(50L);
        assertThat(dto.averageDistance()).isEqualTo(75.5);
    }

    @Test
    void toGlobalMetricsDTO_whenNull_shouldReturnNull() {
        MetricsRepository.GlobalMetricsProjection projection = null;

        GlobalMetricsDTO dto = metricsMapper.toGlobalMetricsDTO(projection);

        assertThat(dto).isNull();
    }

    @Test
    void toUnfulfilledDemandDTO_shouldReturnDTO() {
        MetricsRepository.UnfulfilledDemandProjection projection = mock(MetricsRepository.UnfulfilledDemandProjection.class);
        when(projection.getTotalUnits()).thenReturn(100L);
        when(projection.getUnitsByStockShortage()).thenReturn(60L);
        when(projection.getUnitsByCapacityShortage()).thenReturn(40L);

        UnfulfilledDemandDTO dto = metricsMapper.toUnfulfilledDemandDTO(projection);

        assertThat(dto).isNotNull();
        assertThat(dto.totalUnits()).isEqualTo(100L);
        assertThat(dto.unitsByStockShortage()).isEqualTo(60L);
        assertThat(dto.unitsByCapacityShortage()).isEqualTo(40L);
    }

    @Test
    void toUnfulfilledDemandDTO_whenNull_shouldReturnNull() {
        MetricsRepository.UnfulfilledDemandProjection projection = null;

        UnfulfilledDemandDTO dto = metricsMapper.toUnfulfilledDemandDTO(projection);

        assertThat(dto).isNull();
    }

    @Test
    void toDistanceStatsDTO_shouldReturnDTO() {
        MetricsRepository.DistanceStatsProjection projection = mock(MetricsRepository.DistanceStatsProjection.class);
        when(projection.getMinDistance()).thenReturn(10.0);
        when(projection.getMaxDistance()).thenReturn(200.0);
        when(projection.getMedianDistance()).thenReturn(75.5);

        DistanceStatsDTO dto = metricsMapper.toDistanceStatsDTO(projection);

        assertThat(dto).isNotNull();
        assertThat(dto.minDistance()).isEqualTo(10.0);
        assertThat(dto.maxDistance()).isEqualTo(200.0);
        assertThat(dto.medianDistance()).isEqualTo(75.5);
    }

    @Test
    void toDistanceStatsDTO_whenNull_shouldReturnNull() {
        MetricsRepository.DistanceStatsProjection projection = null;

        DistanceStatsDTO dto = metricsMapper.toDistanceStatsDTO(projection);

        assertThat(dto).isNull();
    }

    @Test
    void toWarehouseUnitsDTO_shouldReturnDTO() {
        MetricsRepository.WarehouseMetricsProjection projection = mock(MetricsRepository.WarehouseMetricsProjection.class);
        when(projection.getWarehouseId()).thenReturn("W1");
        when(projection.getTotalUnits()).thenReturn(500L);
        when(projection.getPercentage()).thenReturn(50.0);
        when(projection.getAvgDistance()).thenReturn(75.5);

        WarehouseUnitsDTO dto = metricsMapper.toWarehouseUnitsDTO(projection);

        assertThat(dto).isNotNull();
        assertThat(dto.warehouseId()).isEqualTo("W1");
        assertThat(dto.totalUnits()).isEqualTo(500L);
        assertThat(dto.percentage()).isEqualTo(50.0);
        assertThat(dto.avgDistance()).isEqualTo(75.5);
    }

    @Test
    void toWarehouseUnitsDTO_whenNull_shouldReturnNull() {
        MetricsRepository.WarehouseMetricsProjection projection = null;

        WarehouseUnitsDTO dto = metricsMapper.toWarehouseUnitsDTO(projection);

        assertThat(dto).isNull();
    }

    @Test
    void toTopProductDTO_shouldReturnDTO() {
        MetricsRepository.TopProductProjection projection = mock(MetricsRepository.TopProductProjection.class);
        when(projection.getProductId()).thenReturn("P1");
        when(projection.getTotalQuantity()).thenReturn(1000L);

        TopProductDTO dto = metricsMapper.toTopProductDTO(projection);

        assertThat(dto).isNotNull();
        assertThat(dto.productId()).isEqualTo("P1");
        assertThat(dto.totalQuantity()).isEqualTo(1000L);
    }

    @Test
    void toTopProductDTO_whenNull_shouldReturnNull() {
        MetricsRepository.TopProductProjection projection = null;

        TopProductDTO dto = metricsMapper.toTopProductDTO(projection);

        assertThat(dto).isNull();
    }

    @Test
    void toWarehouseUnitsDTOList_shouldReturnDTOList() {
        MetricsRepository.WarehouseMetricsProjection projection1 = mock(MetricsRepository.WarehouseMetricsProjection.class);
        when(projection1.getWarehouseId()).thenReturn("W1");
        when(projection1.getTotalUnits()).thenReturn(500L);
        when(projection1.getPercentage()).thenReturn(50.0);
        when(projection1.getAvgDistance()).thenReturn(75.5);

        MetricsRepository.WarehouseMetricsProjection projection2 = mock(MetricsRepository.WarehouseMetricsProjection.class);
        when(projection2.getWarehouseId()).thenReturn("W2");
        when(projection2.getTotalUnits()).thenReturn(300L);
        when(projection2.getPercentage()).thenReturn(30.0);
        when(projection2.getAvgDistance()).thenReturn(50.0);

        List<MetricsRepository.WarehouseMetricsProjection> projections = List.of(projection1, projection2);

        List<WarehouseUnitsDTO> dtos = metricsMapper.toWarehouseUnitsDTOList(projections);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).warehouseId()).isEqualTo("W1");
        assertThat(dtos.get(0).totalUnits()).isEqualTo(500L);
        assertThat(dtos.get(1).warehouseId()).isEqualTo("W2");
        assertThat(dtos.get(1).totalUnits()).isEqualTo(300L);
    }

    @Test
    void toWarehouseUnitsDTOList_whenNull_shouldReturnNull() {
        List<MetricsRepository.WarehouseMetricsProjection> projections = null;

        List<WarehouseUnitsDTO> dtos = metricsMapper.toWarehouseUnitsDTOList(projections);

        assertThat(dtos).isNull();
    }

    @Test
    void toWarehouseUnitsDTOList_whenEmptyList_shouldReturnEmptyList() {
        List<MetricsRepository.WarehouseMetricsProjection> projections = List.of();

        List<WarehouseUnitsDTO> dtos = metricsMapper.toWarehouseUnitsDTOList(projections);

        assertThat(dtos).isEmpty();
    }

    @Test
    void toTopProductDTOList_shouldReturnDTOList() {
        MetricsRepository.TopProductProjection projection1 = mock(MetricsRepository.TopProductProjection.class);
        when(projection1.getProductId()).thenReturn("P1");
        when(projection1.getTotalQuantity()).thenReturn(1000L);

        MetricsRepository.TopProductProjection projection2 = mock(MetricsRepository.TopProductProjection.class);
        when(projection2.getProductId()).thenReturn("P2");
        when(projection2.getTotalQuantity()).thenReturn(800L);

        List<MetricsRepository.TopProductProjection> projections = List.of(projection1, projection2);

        List<TopProductDTO> dtos = metricsMapper.toTopProductDTOList(projections);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).productId()).isEqualTo("P1");
        assertThat(dtos.get(0).totalQuantity()).isEqualTo(1000L);
        assertThat(dtos.get(1).productId()).isEqualTo("P2");
        assertThat(dtos.get(1).totalQuantity()).isEqualTo(800L);
    }

    @Test
    void toTopProductDTOList_whenNull_shouldReturnNull() {
        List<MetricsRepository.TopProductProjection> projections = null;

        List<TopProductDTO> dtos = metricsMapper.toTopProductDTOList(projections);

        assertThat(dtos).isNull();
    }

    @Test
    void toTopProductDTOList_whenEmptyList_shouldReturnEmptyList() {
        List<MetricsRepository.TopProductProjection> projections = List.of();

        List<TopProductDTO> dtos = metricsMapper.toTopProductDTOList(projections);

        assertThat(dtos).isEmpty();
    }

    @Test
    void toStoresServedDTO_shouldCalculateCorrectly() {
        MetricsRepository.DetailedMetricsProjection projection = mock(MetricsRepository.DetailedMetricsProjection.class);
        when(projection.getServedStores()).thenReturn(80);
        when(projection.getFullyServedStores()).thenReturn(60);
        when(projection.getTotalStores()).thenReturn(100);

        StoresServedDTO dto = metricsMapper.toStoresServedDTO(projection);

        assertThat(dto).isNotNull();
        assertThat(dto.servedStores()).isEqualTo(80);
        assertThat(dto.fullyServedStores()).isEqualTo(60);
        assertThat(dto.neverServedStores()).isEqualTo(20);
        assertThat(dto.coveragePercentage()).isEqualTo(80.0);
        assertThat(dto.fullyServedPercentage()).isEqualTo(60.0);
    }

    @Test
    void toStoresServedDTO_whenNull_shouldReturnNull() {
        MetricsRepository.DetailedMetricsProjection projection = null;

        StoresServedDTO dto = metricsMapper.toStoresServedDTO(projection);

        assertThat(dto).isNull();
    }

    @Test
    void toStoresServedDTO_whenZeroTotalStores_shouldReturnZeroPercentages() {
        MetricsRepository.DetailedMetricsProjection projection = mock(MetricsRepository.DetailedMetricsProjection.class);
        when(projection.getServedStores()).thenReturn(0);
        when(projection.getFullyServedStores()).thenReturn(0);
        when(projection.getTotalStores()).thenReturn(0);

        StoresServedDTO dto = metricsMapper.toStoresServedDTO(projection);

        assertThat(dto).isNotNull();
        assertThat(dto.servedStores()).isZero();
        assertThat(dto.fullyServedStores()).isZero();
        assertThat(dto.neverServedStores()).isZero();
        assertThat(dto.coveragePercentage()).isZero();
        assertThat(dto.fullyServedPercentage()).isZero();
    }

    @Test
    void toCapacityUtilizationDTO_shouldCalculateCorrectly() {
        MetricsRepository.CapacityUtilizationProjection projection = mock(MetricsRepository.CapacityUtilizationProjection.class);
        when(projection.getUsedCapacity()).thenReturn(750L);
        when(projection.getTotalCapacity()).thenReturn(1000L);
        when(projection.getStoresAtCapacity()).thenReturn(5);
        when(projection.getTotalStores()).thenReturn(10);

        CapacityUtilizationDTO dto = metricsMapper.toCapacityUtilizationDTO(projection);

        assertThat(dto).isNotNull();
        assertThat(dto.percentage()).isEqualTo(75.0);
        assertThat(dto.storesAtCapacity()).isEqualTo(5);
        assertThat(dto.totalStores()).isEqualTo(10);
    }

    @Test
    void toCapacityUtilizationDTO_whenNull_shouldReturnNull() {
        MetricsRepository.CapacityUtilizationProjection projection = null;

        CapacityUtilizationDTO dto = metricsMapper.toCapacityUtilizationDTO(projection);

        assertThat(dto).isNull();
    }

    @Test
    void toCapacityUtilizationDTO_whenZeroTotalCapacity_shouldReturnZeroPercentage() {
        MetricsRepository.CapacityUtilizationProjection projection = mock(MetricsRepository.CapacityUtilizationProjection.class);
        when(projection.getUsedCapacity()).thenReturn(0L);
        when(projection.getTotalCapacity()).thenReturn(0L);
        when(projection.getStoresAtCapacity()).thenReturn(0);
        when(projection.getTotalStores()).thenReturn(0);

        CapacityUtilizationDTO dto = metricsMapper.toCapacityUtilizationDTO(projection);

        assertThat(dto).isNotNull();
        assertThat(dto.percentage()).isZero();
        assertThat(dto.storesAtCapacity()).isZero();
        assertThat(dto.totalStores()).isZero();
    }
}