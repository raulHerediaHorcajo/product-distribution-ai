package com.productdistribution.backend.services.strategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.services.GeoDistanceService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy that orders warehouses considering distance tolerance and specific product stock.
 */
@Component("distanceWithToleranceStrategy")
public class DistanceWithToleranceStrategy implements WarehouseSelectionStrategy {
    
    private static final double DISTANCE_TOLERANCE = 0.10;
    
    private final GeoDistanceService geoDistanceService;
    
    @Autowired
    public DistanceWithToleranceStrategy(GeoDistanceService geoDistanceService) {
        this.geoDistanceService = geoDistanceService;
    }
    
    @Override
    public List<WarehouseWithDistance> selectWarehouses(Store store, List<Warehouse> warehouses, 
                                                       String productId, String size) {
        List<WarehouseWithDistance> warehousesWithDistance = warehouses.stream()
            .map(warehouse -> {
                double distance = geoDistanceService.calculateHaversineDistance(
                    store.getLatitude(), store.getLongitude(),
                    warehouse.getLatitude(), warehouse.getLongitude()
                );
                return new WarehouseWithDistance(warehouse, distance);
            })
            .collect(Collectors.toList());
        
        warehousesWithDistance.sort((w1, w2) -> {
            double d1 = w1.distanceKm();
            double d2 = w2.distanceKm();
            double diff = Math.abs(d1 - d2);
            
            if (diff >= Math.min(d1, d2) * DISTANCE_TOLERANCE) return Double.compare(d1, d2);
            
            int stock1 = w1.warehouse().getStockForProduct(productId, size);
            int stock2 = w2.warehouse().getStockForProduct(productId, size);
            return Integer.compare(stock2, stock1);
        });
        
        return warehousesWithDistance;
    }
}