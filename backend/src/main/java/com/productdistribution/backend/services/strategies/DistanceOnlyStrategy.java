package com.productdistribution.backend.services.strategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.services.GeoDistanceService;

import java.util.Comparator;
import java.util.List;

/**
 * Strategy that orders warehouses solely by distance.
 */
@Component("distanceOnlyStrategy")
public class DistanceOnlyStrategy implements WarehouseSelectionStrategy {
    
    private final GeoDistanceService geoDistanceService;
    
    @Autowired
    public DistanceOnlyStrategy(GeoDistanceService geoDistanceService) {
        this.geoDistanceService = geoDistanceService;
    }
    
    @Override
    public List<WarehouseWithDistance> selectWarehouses(Store store, List<Warehouse> warehouses, 
                                                       String productId, String size) {
        return warehouses.stream()
            .map(warehouse -> {
                double distance = geoDistanceService.calculateHaversineDistance(
                    store.getLatitude(), store.getLongitude(),
                    warehouse.getLatitude(), warehouse.getLongitude()
                );
                return new WarehouseWithDistance(warehouse, distance);
            })
            .sorted(Comparator.comparingDouble(WarehouseWithDistance::distanceKm))
            .toList();
    }
}