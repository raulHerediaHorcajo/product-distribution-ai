package com.productdistribution.backend.mappers;

import org.mapstruct.Mapper;

import com.productdistribution.backend.dtos.metrics.*;
import com.productdistribution.backend.repositories.MetricsRepository;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetricsMapper {
    GlobalMetricsDTO toGlobalMetricsDTO(MetricsRepository.GlobalMetricsProjection projection);
    UnfulfilledDemandDTO toUnfulfilledDemandDTO(MetricsRepository.UnfulfilledDemandProjection projection);
    DistanceStatsDTO toDistanceStatsDTO(MetricsRepository.DistanceStatsProjection projection);
    WarehouseUnitsDTO toWarehouseUnitsDTO(MetricsRepository.WarehouseMetricsProjection projection);
    TopProductDTO toTopProductDTO(MetricsRepository.TopProductProjection projection);
    List<WarehouseUnitsDTO> toWarehouseUnitsDTOList(List<MetricsRepository.WarehouseMetricsProjection> projections);
    List<TopProductDTO> toTopProductDTOList(List<MetricsRepository.TopProductProjection> projections);

    default StoresServedDTO toStoresServedDTO(MetricsRepository.DetailedMetricsProjection projection) {
        if (projection == null) {
            return null;
        }
        
        int servedStores = projection.getServedStores();
        int fullyServedStores = projection.getFullyServedStores();
        int totalStores = projection.getTotalStores();
        int neverServedStores = totalStores - servedStores;
        
        double coveragePercentage = calculatePercentage(servedStores, totalStores);
        double fullyServedPercentage = calculatePercentage(fullyServedStores, totalStores);
        
        return new StoresServedDTO(
            servedStores,
            fullyServedStores,
            neverServedStores,
            coveragePercentage,
            fullyServedPercentage
        );
    }

    default CapacityUtilizationDTO toCapacityUtilizationDTO(MetricsRepository.CapacityUtilizationProjection projection) {
        if (projection == null) {
            return null;
        }
        
        double capacityPercentage = calculatePercentage(projection.getUsedCapacity(), projection.getTotalCapacity());
        
        return new CapacityUtilizationDTO(
            capacityPercentage,
            projection.getStoresAtCapacity(),
            projection.getTotalStores()
        );
    }

    private static double calculatePercentage(long numerator, long denominator) {
        return denominator > 0 ? (double) numerator / denominator * 100.0 : 0.0;
    }
}