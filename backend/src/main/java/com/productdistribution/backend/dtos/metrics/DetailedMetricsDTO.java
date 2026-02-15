package com.productdistribution.backend.dtos.metrics;

import java.util.List;

public record DetailedMetricsDTO(
    Integer efficiencyScore,
    Double fulfillmentRate,
    Double totalDistance,
    Long totalShipments,
    StoresServedDTO storesServed,
    Double avgShipmentSize,
    Integer uniqueProductsDistributed,
    Integer uniqueProductsRequested,
    CapacityUtilizationDTO capacityUtilization,
    UnfulfilledDemandDTO unfulfilledDemand,
    Long fulfilledUnits,
    DistanceStatsDTO distanceStats,
    List<WarehouseUnitsDTO> unitsByWarehouse,
    List<TopProductDTO> topProducts
) {}