package com.productdistribution.backend.unit.services;

import com.productdistribution.backend.entities.*;
import com.productdistribution.backend.exceptions.ConfigurationException;
import com.productdistribution.backend.services.*;
import com.productdistribution.backend.services.strategies.WarehouseSelectionStrategy;
import com.productdistribution.backend.services.strategies.WarehouseWithDistance;
import com.productdistribution.backend.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistributionServiceUnitTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private StoreService storeService;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private ProductService productService;

    @Mock
    private StockAssignmentService stockAssignmentService;

    @Mock
    private UnfulfilledDemandService unfulfilledDemandService;

    @Mock
    private WarehouseSelectionStrategy warehouseSelectionStrategy;

    @InjectMocks
    private DistributionService distributionService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(distributionService, "strategyName", "distanceOnlyStrategy");
    }

    @Test
    void distributeProducts_shouldReturnStockAssignments() {
        Store store = StoreBuilder.store1();
        Warehouse warehouse = WarehouseBuilder.warehouse1();
        Product product = ProductBuilder.product1();
        List<Store> stores = List.of(store);
        List<Warehouse> warehouses = List.of(warehouse);
        List<Product> products = List.of(product);
        List<WarehouseWithDistance> sortedWarehouses = List.of(new WarehouseWithDistance(warehouse, 100.0));
        List<StockAssignment> expectedAssignments = List.of(StockAssignmentBuilder.stockAssignment1(), StockAssignmentBuilder.stockAssignment2());

        when(applicationContext.getBean("distanceOnlyStrategy", WarehouseSelectionStrategy.class))
                .thenReturn(warehouseSelectionStrategy);
        when(storeService.refreshStoresFromJson()).thenReturn(stores);
        when(warehouseService.refreshWarehousesFromJson()).thenReturn(warehouses);
        when(productService.refreshProductsFromJson()).thenReturn(products);
        when(warehouseSelectionStrategy.selectWarehouses(store, warehouses, store.getDemand().get(0).getProductId(),
                store.getDemand().get(0).getSize()))
                .thenReturn(sortedWarehouses);
        when(warehouseSelectionStrategy.selectWarehouses(store, warehouses, store.getDemand().get(1).getProductId(),
                store.getDemand().get(1).getSize()))
                .thenReturn(sortedWarehouses);

        List<StockAssignment> result = distributionService.distributeProducts();

        verify(applicationContext, times(3)).getBean("distanceOnlyStrategy", WarehouseSelectionStrategy.class);
        verify(storeService).refreshStoresFromJson();
        verify(warehouseService).refreshWarehousesFromJson();
        verify(productService).refreshProductsFromJson();
        verify(warehouseSelectionStrategy).selectWarehouses(store, warehouses, store.getDemand().get(0).getProductId(),
                store.getDemand().get(0).getSize());
        verify(warehouseSelectionStrategy).selectWarehouses(store, warehouses, store.getDemand().get(1).getProductId(),
                store.getDemand().get(1).getSize());
        verify(stockAssignmentService).deleteAll();
        verify(stockAssignmentService).addAll(anyList());
        verify(unfulfilledDemandService).deleteAll();
        verify(unfulfilledDemandService).addAll(List.of());
        verify(storeService).addAll(stores);
        verify(warehouseService).addAll(warehouses);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedAssignments);
    }

    @Test
    void distributeProducts_whenStrategyNotFound_shouldThrowConfigurationException() {
        when(applicationContext.getBean("distanceOnlyStrategy", WarehouseSelectionStrategy.class))
                .thenThrow(new NoSuchBeanDefinitionException("distanceOnlyStrategy"));

        assertThatThrownBy(() -> distributionService.distributeProducts())
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("Warehouse selection strategy 'distanceOnlyStrategy' not found");

        verify(applicationContext).getBean("distanceOnlyStrategy", WarehouseSelectionStrategy.class);
        verify(storeService, never()).refreshStoresFromJson();
        verify(warehouseService, never()).refreshWarehousesFromJson();
        verify(productService, never()).refreshProductsFromJson();
    }

    @Test
    void distributeProducts_whenQuantityToSendIsZero_shouldSkipAssignmentAndCreateUnfulfilledDemand() {
        Store store = StoreBuilder.builder()
                .withId("S1")
                .withMaxStockCapacity(10)
                .withRemainingCapacity(0)
                .withDemand(List.of(ProductItemBuilder.productItem1()))
                .build();
        Warehouse warehouse = WarehouseBuilder.warehouse1();
        Product product = ProductBuilder.product1();
        List<Store> stores = List.of(store);
        List<Warehouse> warehouses = List.of(warehouse);
        List<Product> products = List.of(product);

        when(applicationContext.getBean("distanceOnlyStrategy", WarehouseSelectionStrategy.class))
                .thenReturn(warehouseSelectionStrategy);
        when(storeService.refreshStoresFromJson()).thenReturn(stores);
        when(warehouseService.refreshWarehousesFromJson()).thenReturn(warehouses);
        when(productService.refreshProductsFromJson()).thenReturn(products);
        when(warehouseSelectionStrategy.selectWarehouses(store, warehouses, store.getDemand().get(0).getProductId(),
                store.getDemand().get(0).getSize()))
                .thenReturn(List.of(new WarehouseWithDistance(warehouse, 100.0)));

        List<StockAssignment> result = distributionService.distributeProducts();

        verify(unfulfilledDemandService).addAll(anyList());
        assertThat(result).isEmpty();
    }

    @Test
    void distributeProducts_whenTryAllocateCapacityFails_shouldSkipAssignmentAndCreateUnfulfilledDemand() {
        Store store = mock(Store.class);
        when(store.getId()).thenReturn("S1");
        when(store.getMaxStockCapacity()).thenReturn(100);
        when(store.getRemainingCapacity()).thenReturn(50);
        when(store.getDemand()).thenReturn(List.of(ProductItemBuilder.productItem1()));
        when(store.calculateAdjustedDemand(50)).thenReturn(45);
        when(store.tryAllocateCapacity(45)).thenReturn(false);
        
        Warehouse warehouse = WarehouseBuilder.warehouse1();
        Product product = ProductBuilder.product1();
        List<Store> stores = List.of(store);
        List<Warehouse> warehouses = List.of(warehouse);
        List<Product> products = List.of(product);

        when(applicationContext.getBean("distanceOnlyStrategy", WarehouseSelectionStrategy.class))
                .thenReturn(warehouseSelectionStrategy);
        when(storeService.refreshStoresFromJson()).thenReturn(stores);
        when(warehouseService.refreshWarehousesFromJson()).thenReturn(warehouses);
        when(productService.refreshProductsFromJson()).thenReturn(products);
        when(warehouseSelectionStrategy.selectWarehouses(store, warehouses, store.getDemand().get(0).getProductId(),
                store.getDemand().get(0).getSize()))
                .thenReturn(List.of(new WarehouseWithDistance(warehouse, 100.0)));

        List<StockAssignment> result = distributionService.distributeProducts();

        verify(store).tryAllocateCapacity(45);
        verify(unfulfilledDemandService).addAll(anyList());
        assertThat(result).isEmpty();
    }
}