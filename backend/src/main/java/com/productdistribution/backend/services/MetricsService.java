package com.productdistribution.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.productdistribution.backend.dtos.metrics.*;
import com.productdistribution.backend.mappers.MetricsMapper;
import com.productdistribution.backend.repositories.MetricsRepository;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;

import java.util.List;

@Service
public class MetricsService {

    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);
    
    // Distance threshold for normalization (covers European and some international routes)
    private static final double MAX_DISTANCE_FOR_NORMALIZATION = 5000.0;
    
    // Weights adjusted so fulfillment has greater importance in efficiency score
    private static final double FULFILLMENT_RATE_WEIGHT = 0.7;
    private static final double DISTANCE_WEIGHT = 0.2;
    private static final double CAPACITY_WEIGHT = 0.1;

    private final MetricsRepository metricsRepository;
    private final MetricsMapper metricsMapper;

    @Autowired
    public MetricsService(MetricsRepository metricsRepository, MetricsMapper metricsMapper) {
        this.metricsRepository = metricsRepository;
        this.metricsMapper = metricsMapper;
    }

    @Cacheable("globalMetrics")
    public GlobalMetricsDTO calculateGlobalMetrics() {
        logger.info("Calculating global metrics");
        
        try {
            MetricsRepository.GlobalMetricsProjection projection = metricsRepository.getGlobalMetrics();
            GlobalMetricsDTO result = metricsMapper.toGlobalMetricsDTO(projection);
            
            logger.info("Global metrics calculated: {} shipments, {} units, {} km average, {} unfulfilled units",
                result.totalShipments(), result.fulfilledUnits(), 
                result.averageDistance(), result.unfulfilledUnits());
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error calculating global metrics", e);
            return createEmptyGlobalMetrics();
        }
    }

    @Cacheable(value = "detailedMetrics", key = "#criteria.warehouseId + '_' + #criteria.storeId + '_' + #criteria.productId")
    public DetailedMetricsDTO calculateDetailedMetrics(StockAssignmentCriteria criteria) {
        logger.info("Calculating detailed metrics");
        
        try {
            String warehouseId = criteria.getWarehouseId();
            String storeId = criteria.getStoreId();
            String productId = criteria.getProductId();
            
            MetricsRepository.DetailedMetricsProjection basicMetrics = metricsRepository.getDetailedMetrics(
                warehouseId, storeId, productId);
            Double fulfillmentRate = calculateFulfillmentRate(
                basicMetrics.getTotalFulfilled(), basicMetrics.getTotalUnfulfilled());
            StoresServedDTO storesServed = metricsMapper.toStoresServedDTO(basicMetrics);
            
            MetricsRepository.UnfulfilledDemandProjection unfulfilledProjection = 
                metricsRepository.getUnfulfilledDemand(warehouseId, storeId, productId);
            UnfulfilledDemandDTO unfulfilledDemand = metricsMapper.toUnfulfilledDemandDTO(unfulfilledProjection);
            
            MetricsRepository.DistanceStatsProjection distanceProjection = 
                metricsRepository.getDistanceStats(warehouseId, storeId, productId);
            DistanceStatsDTO distanceStats = metricsMapper.toDistanceStatsDTO(distanceProjection);
            
            MetricsRepository.CapacityUtilizationProjection capacityProjection = 
                metricsRepository.getCapacityUtilization(warehouseId, storeId, productId);
            CapacityUtilizationDTO capacityUtilization = metricsMapper.toCapacityUtilizationDTO(capacityProjection);
            
            List<MetricsRepository.WarehouseMetricsProjection> warehouseProjections = 
                metricsRepository.getWarehouseMetrics(warehouseId, storeId, productId);
            List<WarehouseUnitsDTO> unitsByWarehouse = metricsMapper.toWarehouseUnitsDTOList(warehouseProjections);
            
            List<MetricsRepository.TopProductProjection> topProductProjections = 
                metricsRepository.getTopProducts(warehouseId, storeId, productId);
            List<TopProductDTO> topProducts = metricsMapper.toTopProductDTOList(topProductProjections);
            
            Double weightedAverageDistance = calculateWeightedAverageDistance(unitsByWarehouse);
            Integer efficiencyScore = calculateEfficiencyScore(
                fulfillmentRate, weightedAverageDistance, capacityUtilization.percentage());
            
            logger.info("Detailed metrics calculated: {} assignments, {}% fulfillment rate, {} efficiency score",
                basicMetrics.getTotalFulfilled(), fulfillmentRate, efficiencyScore);
            
            return new DetailedMetricsDTO(
                efficiencyScore,
                fulfillmentRate,
                basicMetrics.getTotalDistance(),
                basicMetrics.getTotalShipments(),
                storesServed,
                basicMetrics.getAvgShipmentSize(),
                basicMetrics.getUniqueProductsDistributed(),
                basicMetrics.getUniqueProductsRequested(),
                capacityUtilization,
                unfulfilledDemand,
                basicMetrics.getTotalFulfilled(),
                distanceStats,
                unitsByWarehouse,
                topProducts
            );
            
        } catch (Exception e) {
            logger.error("Error calculating detailed metrics", e);
            return createEmptyDetailedMetrics();
        }
    }

    private Double calculateFulfillmentRate(Long totalShipped, Long totalUnfulfilled) {
        long totalDemanded = totalShipped + totalUnfulfilled;
        if (totalDemanded == 0) return 0.0;
        return (double) totalShipped / totalDemanded * 100.0;
    }

    private Double calculateWeightedAverageDistance(List<WarehouseUnitsDTO> unitsByWarehouse) {
        if (unitsByWarehouse == null || unitsByWarehouse.isEmpty()) {
            return 0.0;
        }
        
        double totalWeightedDistance = 0.0;
        long totalUnits = 0;
        
        for (WarehouseUnitsDTO warehouse : unitsByWarehouse) {
            totalWeightedDistance += warehouse.totalUnits() * warehouse.avgDistance();
            totalUnits += warehouse.totalUnits();
        }
        
        if (totalUnits == 0) return 0.0;
        return totalWeightedDistance / totalUnits;
    }

    private Integer calculateEfficiencyScore(Double fulfillmentRate, Double averageDistance, Double capacityUtilization) {
        double normalizedDistance = Math.max(0, 1 - (averageDistance / MAX_DISTANCE_FOR_NORMALIZATION));
        double normalizedDistancePercent = normalizedDistance * 100.0;
        
        double score = (fulfillmentRate * FULFILLMENT_RATE_WEIGHT) + 
                      (normalizedDistancePercent * DISTANCE_WEIGHT) + 
                      (capacityUtilization * CAPACITY_WEIGHT);
        
        return Math.max(0, Math.min(100, (int) Math.round(score)));
    }

    private DetailedMetricsDTO createEmptyDetailedMetrics() {
        return new DetailedMetricsDTO(
            0, 0.0, 0.0, 0L,
            new StoresServedDTO(0, 0, 0, 0.0, 0.0),
            0.0, 0, 0,
            new CapacityUtilizationDTO(0.0, 0, 0),
            new UnfulfilledDemandDTO(0L, 0L, 0L),
            0L,
            new DistanceStatsDTO(0.0, 0.0, 0.0),
            List.of(), List.of()
        );
    }

    private GlobalMetricsDTO createEmptyGlobalMetrics() {
        return new GlobalMetricsDTO(0L, 0L, 0L, 0.0);
    }
}