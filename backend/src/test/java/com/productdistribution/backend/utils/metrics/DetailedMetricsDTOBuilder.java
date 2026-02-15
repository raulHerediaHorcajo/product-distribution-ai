package com.productdistribution.backend.utils.metrics;

import com.productdistribution.backend.dtos.metrics.DetailedMetricsDTO;
import com.productdistribution.backend.dtos.metrics.StoresServedDTO;
import com.productdistribution.backend.dtos.metrics.CapacityUtilizationDTO;
import com.productdistribution.backend.dtos.metrics.UnfulfilledDemandDTO;
import com.productdistribution.backend.dtos.metrics.DistanceStatsDTO;
import com.productdistribution.backend.dtos.metrics.WarehouseUnitsDTO;
import com.productdistribution.backend.dtos.metrics.TopProductDTO;

import java.util.ArrayList;
import java.util.List;

public class DetailedMetricsDTOBuilder {

    private Integer efficiencyScore = 0;
    private Double fulfillmentRate = 0.0;
    private Double totalDistance = 0.0;
    private Long totalShipments = 0L;
    private StoresServedDTO storesServed = StoresServedDTOBuilder.defaultStoresServed();
    private Double avgShipmentSize = 0.0;
    private Integer uniqueProductsDistributed = 0;
    private Integer uniqueProductsRequested = 0;
    private CapacityUtilizationDTO capacityUtilization = CapacityUtilizationDTOBuilder.defaultCapacityUtilization();
    private UnfulfilledDemandDTO unfulfilledDemand = UnfulfilledDemandDTOBuilder.defaultUnfulfilledDemand();
    private Long fulfilledUnits = 0L;
    private DistanceStatsDTO distanceStats = DistanceStatsDTOBuilder.defaultDistanceStats();
    private List<WarehouseUnitsDTO> unitsByWarehouse = new ArrayList<>();
    private List<TopProductDTO> topProducts = new ArrayList<>();

    private DetailedMetricsDTOBuilder() {
        // Prevent direct instantiation - use builder()
    }

    public static DetailedMetricsDTOBuilder builder() {
        return new DetailedMetricsDTOBuilder();
    }

    public DetailedMetricsDTOBuilder withEfficiencyScore(Integer efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
        return this;
    }

    public DetailedMetricsDTOBuilder withFulfillmentRate(Double fulfillmentRate) {
        this.fulfillmentRate = fulfillmentRate;
        return this;
    }

    public DetailedMetricsDTOBuilder withTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
        return this;
    }

    public DetailedMetricsDTOBuilder withTotalShipments(Long totalShipments) {
        this.totalShipments = totalShipments;
        return this;
    }

    public DetailedMetricsDTOBuilder withStoresServed(StoresServedDTO storesServed) {
        this.storesServed = storesServed;
        return this;
    }

    public DetailedMetricsDTOBuilder withAvgShipmentSize(Double avgShipmentSize) {
        this.avgShipmentSize = avgShipmentSize;
        return this;
    }

    public DetailedMetricsDTOBuilder withUniqueProductsDistributed(Integer uniqueProductsDistributed) {
        this.uniqueProductsDistributed = uniqueProductsDistributed;
        return this;
    }

    public DetailedMetricsDTOBuilder withUniqueProductsRequested(Integer uniqueProductsRequested) {
        this.uniqueProductsRequested = uniqueProductsRequested;
        return this;
    }

    public DetailedMetricsDTOBuilder withCapacityUtilization(CapacityUtilizationDTO capacityUtilization) {
        this.capacityUtilization = capacityUtilization;
        return this;
    }

    public DetailedMetricsDTOBuilder withUnfulfilledDemand(UnfulfilledDemandDTO unfulfilledDemand) {
        this.unfulfilledDemand = unfulfilledDemand;
        return this;
    }

    public DetailedMetricsDTOBuilder withFulfilledUnits(Long fulfilledUnits) {
        this.fulfilledUnits = fulfilledUnits;
        return this;
    }

    public DetailedMetricsDTOBuilder withDistanceStats(DistanceStatsDTO distanceStats) {
        this.distanceStats = distanceStats;
        return this;
    }

    public DetailedMetricsDTOBuilder withUnitsByWarehouse(List<WarehouseUnitsDTO> unitsByWarehouse) {
        this.unitsByWarehouse = new ArrayList<>(unitsByWarehouse);
        return this;
    }

    public DetailedMetricsDTOBuilder withTopProducts(List<TopProductDTO> topProducts) {
        this.topProducts = new ArrayList<>(topProducts);
        return this;
    }

    public DetailedMetricsDTO build() {
        return new DetailedMetricsDTO(
                efficiencyScore,
                fulfillmentRate,
                totalDistance,
                totalShipments,
                storesServed,
                avgShipmentSize,
                uniqueProductsDistributed,
                uniqueProductsRequested,
                capacityUtilization,
                unfulfilledDemand,
                fulfilledUnits,
                distanceStats,
                unitsByWarehouse,
                topProducts
        );
    }

    public static DetailedMetricsDTO defaultDetailedMetrics() {
        return builder().build();
    }

    public static DetailedMetricsDTO detailedMetrics1() {
        return builder()
                .withEfficiencyScore(85)
                .withFulfillmentRate(90.5)
                .withTotalDistance(1500.0)
                .withTotalShipments(200L)
                .withStoresServed(StoresServedDTOBuilder.storesServed1())
                .withAvgShipmentSize(15.5)
                .withUniqueProductsDistributed(25)
                .withUniqueProductsRequested(30)
                .withCapacityUtilization(CapacityUtilizationDTOBuilder.capacityUtilization1())
                .withUnfulfilledDemand(UnfulfilledDemandDTOBuilder.unfulfilledDemand1())
                .withFulfilledUnits(500L)
                .withDistanceStats(DistanceStatsDTOBuilder.distanceStats1())
                .withUnitsByWarehouse(List.of(
                        WarehouseUnitsDTOBuilder.warehouseUnits1(),
                        WarehouseUnitsDTOBuilder.warehouseUnits2()
                ))
                .withTopProducts(List.of(
                        TopProductDTOBuilder.topProduct1(),
                        TopProductDTOBuilder.topProduct2()
                ))
                .build();
    }

    /**
     * Expected DetailedMetricsDTO for the integration scenario using:
     * StoreBuilder(store1, store2),
     * WarehouseBuilder(warehouse1, warehouse2),
     * StockAssignmentBuilder(1, 2, 3),
     * UnfulfilledDemandBuilder(1, 2).
     *
     * No filters applied.
     */
    public static DetailedMetricsDTO detailedMetricsWhenNoFilters() {
        return builder()
                .withEfficiencyScore(71)
                .withFulfillmentRate(72.44)
                .withTotalDistance(250.0)
                .withTotalShipments(2L)
                .withStoresServed(StoresServedDTOBuilder.storesServedWhenNoFilters())
                .withAvgShipmentSize(46.0)
                .withUniqueProductsDistributed(2)
                .withUniqueProductsRequested(2)
                .withCapacityUtilization(CapacityUtilizationDTOBuilder.capacityUtilizationWhenNoFilters())
                .withUnfulfilledDemand(UnfulfilledDemandDTOBuilder.unfulfilledDemandWhenNoFilters())
                .withFulfilledUnits(92L)
                .withDistanceStats(DistanceStatsDTOBuilder.distanceStatsWhenNoFilters())
                .withUnitsByWarehouse(List.of(
                        WarehouseUnitsDTOBuilder.warehouseUnitsWhenNoFilters()
                ))
                .withTopProducts(List.of(
                        TopProductDTOBuilder.topProductP1WhenNoFilters(),
                        TopProductDTOBuilder.topProductP2WhenNoFilters()
                ))
                .build();
    }
}