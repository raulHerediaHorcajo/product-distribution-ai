package com.productdistribution.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.productdistribution.backend.entities.*;
import com.productdistribution.backend.enums.UnfulfilledReason;
import com.productdistribution.backend.exceptions.ConfigurationException;
import com.productdistribution.backend.services.strategies.WarehouseSelectionStrategy;
import com.productdistribution.backend.services.strategies.WarehouseWithDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DistributionService {

    private static final Logger logger = LoggerFactory.getLogger(DistributionService.class);

    @Value("${distribution.strategy.warehouse-selection:distanceOnlyStrategy}")
    private String strategyName;
    
    private final ApplicationContext applicationContext;
    private final StoreService storeService;
    private final WarehouseService warehouseService;
    private final ProductService productService;
    private final StockAssignmentService stockAssignmentService;
    private final UnfulfilledDemandService unfulfilledDemandService;

    private List<Store> stores;
    private List<Warehouse> warehouses;
    private List<Product> products;

    @Autowired
    public DistributionService(
        ApplicationContext applicationContext,
        StoreService storeService,
        WarehouseService warehouseService,
        ProductService productService,
        StockAssignmentService stockAssignmentService,
        UnfulfilledDemandService unfulfilledDemandService
    ) {
        this.applicationContext = applicationContext;
        this.storeService = storeService;
        this.warehouseService = warehouseService;
        this.productService = productService;
        this.stockAssignmentService = stockAssignmentService;
        this.unfulfilledDemandService = unfulfilledDemandService;
    }

    private void loadData() {
        stores = storeService.refreshStoresFromJson();
        warehouses = warehouseService.refreshWarehousesFromJson();
        products = productService.refreshProductsFromJson();
    }

    private WarehouseSelectionStrategy getWarehouseSelectionStrategy() {
        try {
            return applicationContext.getBean(strategyName, WarehouseSelectionStrategy.class);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new ConfigurationException(
                String.format("Warehouse selection strategy '%s' not found", strategyName), ex);
        }
    }

    private List<WarehouseWithDistance> selectWarehouses(Store store, List<Warehouse> warehouses, 
                                                        String productId, String size) {
        return getWarehouseSelectionStrategy().selectWarehouses(store, warehouses, productId, size);
    }

    private int calculateQuantityToSend(int adjustedDemand, int stockAvailable, int storeCapacityLeft) {
        return Math.min(adjustedDemand, Math.min(stockAvailable, storeCapacityLeft));
    }

    private void processDemandItem(Store store, ProductItem demandItem, 
                                   List<StockAssignment> assignments,
                                   List<UnfulfilledDemand> unfulfilledDemands) {
        
        String productId = demandItem.getProductId();
        String size = demandItem.getSize();
        int originalQuantity = demandItem.getQuantity();
        
        int adjustedDemand = store.calculateAdjustedDemand(originalQuantity);
        
        List<WarehouseWithDistance> sortedWarehouses = selectWarehouses(store, warehouses, productId, size);
        
        for (WarehouseWithDistance warehouseWithDistance : sortedWarehouses) {
            Warehouse warehouse = warehouseWithDistance.warehouse();
            
            Optional<ProductItem> warehouseItemOpt = warehouse.findStock(productId, size)
                .filter(ProductItem::hasStock);
            
            if (warehouseItemOpt.isPresent()) {
                ProductItem warehouseItem = warehouseItemOpt.get();
                int stockAvailable = warehouseItem.getQuantity();
                int quantityToSend = calculateQuantityToSend(adjustedDemand, stockAvailable, store.getRemainingCapacity());
                
                if (quantityToSend <= 0) {
                    continue;
                }
                
                if (!store.tryAllocateCapacity(quantityToSend)) {
                    logger.debug("Store {} does not have sufficient capacity for {} units of product {} size {}",
                        store.getId(), quantityToSend, productId, size);
                    continue;
                }
                
                adjustedDemand -= quantityToSend;
                warehouseItem.reduceQuantity(quantityToSend);
                
                logger.debug("Assigning {} units of product {} size {} to store {} from warehouse {} (distance: {} km)",
                    quantityToSend, productId, size, store.getId(), warehouse.getId(), warehouseWithDistance.distanceKm());
                
                assignments.add(new StockAssignment(
                    store.getId(),
                    warehouse.getId(),
                    productId,
                    size,
                    quantityToSend,
                    warehouseWithDistance.distanceKm()
                ));
            }
            
            if (adjustedDemand == 0) {
                logger.debug("Demand completely satisfied for store {} and product {} size {}", store.getId(), productId, size);
                break;
            }
            
            if (store.getRemainingCapacity() == 0) {
                logger.debug("Store {} has reached maximum capacity, demand partially satisfied", store.getId());
                break;
            }
        }
        
        if (adjustedDemand > 0) {
            UnfulfilledReason reason = store.getRemainingCapacity() == 0 
                ? UnfulfilledReason.CAPACITY_SHORTAGE 
                : UnfulfilledReason.STOCK_SHORTAGE;
            
            String reasonText = reason == UnfulfilledReason.CAPACITY_SHORTAGE 
                ? "insufficient capacity" 
                : "insufficient stock";
            
            logger.warn("Unfulfilled demand due to {}: Store {} - Product {} size {} → {} units missing",
                reasonText, store.getId(), productId, size, adjustedDemand);
            
            unfulfilledDemands.add(new UnfulfilledDemand(
                store.getId(),
                productId,
                size,
                adjustedDemand,
                reason
            ));
        }
    }

    private void processStoreDemand(Store store, List<StockAssignment> assignments,
                                    List<UnfulfilledDemand> unfulfilledDemands) {
        
        logger.debug("Processing demand for store {} (initial capacity: {})", 
            store.getId(), store.getMaxStockCapacity());
        
        for (ProductItem demandItem : store.getDemand()) {
            processDemandItem(store, demandItem, assignments, unfulfilledDemands);
        }
        
        logger.debug("Store {} demand processed (remaining capacity: {})", 
            store.getId(), store.getRemainingCapacity());
    }

    private void persistResults(List<StockAssignment> assignments, List<UnfulfilledDemand> unfulfilledDemands) {
        stockAssignmentService.deleteAll();
        stockAssignmentService.addAll(assignments);
        
        unfulfilledDemandService.deleteAll();
        unfulfilledDemandService.addAll(unfulfilledDemands);
        
        storeService.addAll(stores);
        warehouseService.addAll(warehouses);
    }

    @CacheEvict(value = {"globalMetrics", "detailedMetrics"}, allEntries = true)
    @Transactional
    public List<StockAssignment> distributeProducts() {
        logger.info("Starting product distribution process with strategy: {}", 
            getWarehouseSelectionStrategy().getClass().getSimpleName());
        
        loadData();
        logger.info("Data loaded: {} stores, {} warehouses, {} products", 
            stores.size(), warehouses.size(), products.size());

        List<StockAssignment> assignments = new ArrayList<>();
        List<UnfulfilledDemand> unfulfilledDemands = new ArrayList<>();

        for (Store store : stores) {
            processStoreDemand(store, assignments, unfulfilledDemands);
        }

        persistResults(assignments, unfulfilledDemands);
        
        logger.info("Distribution completed: {} assignments, {} unfulfilled demands", 
            assignments.size(), unfulfilledDemands.size());

        return assignments;
    }
}