package com.productdistribution.backend.services.strategies;

import java.util.List;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.Warehouse;

public interface WarehouseSelectionStrategy {
    
    List<WarehouseWithDistance> selectWarehouses(Store store, List<Warehouse> warehouses, 
                                                String productId, String size);
}