package com.productdistribution.backend.integration.services;

import com.productdistribution.backend.entities.Product;
import com.productdistribution.backend.entities.ProductItem;
import com.productdistribution.backend.entities.StockAssignment;
import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.UnfulfilledDemand;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.enums.UnfulfilledReason;
import com.productdistribution.backend.repositories.StockAssignmentRepository;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.repositories.UnfulfilledDemandRepository;
import com.productdistribution.backend.repositories.WarehouseRepository;
import com.productdistribution.backend.services.DataLoaderService;
import com.productdistribution.backend.services.DistributionService;
import com.productdistribution.backend.utils.ProductBuilder;
import com.productdistribution.backend.utils.ProductItemBuilder;
import com.productdistribution.backend.utils.StockAssignmentBuilder;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.UnfulfilledDemandBuilder;
import com.productdistribution.backend.utils.WarehouseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DistributionServiceIntegrationTest {

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private StockAssignmentRepository stockAssignmentRepository;

    @Autowired
    private UnfulfilledDemandRepository unfulfilledDemandRepository;

    @MockitoBean
    private DataLoaderService dataLoaderService;

    private Product product1;
    private Product product2;
    private Product product3;
    private Store store1;
    private Store store2;
    private Warehouse warehouse1;
    private Warehouse warehouse2;
    private Warehouse warehouse3;

    @BeforeEach
    void setUp() {
        product1 = ProductBuilder.builder().withId("P1").withBrandId("B1")
            .withSizes(List.of("M")).build();
        
        product2 = ProductBuilder.builder().withId("P2").withBrandId("B2")
            .withSizes(List.of("M")).build();

        product3 = ProductBuilder.builder().withId("P3").withBrandId("B3")
            .withSizes(List.of("M")).build();

        store1 = StoreBuilder.builder().withId("S1").withLatitude(40.4168).withLongitude(-3.7038)
            .withCountry("ES").withMaxStockCapacity(100)
            .withExpectedReturnRate(0.1).withRemainingCapacity(100)
            .withDemand(List.of(
                ProductItemBuilder.builder().withProductId("P1")
                    .withSize("M").withQuantity(100).build(),
                ProductItemBuilder.builder().withProductId("P2")
                    .withSize("M").withQuantity(50).build()
            )).build();
        
        store2 = StoreBuilder.builder().withId("S2").withLatitude(48.8566).withLongitude(2.3522)
            .withCountry("ES").withMaxStockCapacity(100)
            .withExpectedReturnRate(0.1).withRemainingCapacity(100)
            .withDemand(List.of(
                ProductItemBuilder.builder().withProductId("P3")
                    .withSize("M").withQuantity(30).build()
            )).build();

        warehouse1 = WarehouseBuilder.builder().withId("W1")
            .withLatitude(44.913501).withLongitude(-3.7038).withCountry("ES")
            .withStock(List.of(
                ProductItemBuilder.builder().withProductId("P1")
                    .withSize("M").withQuantity(50).build()
            )).build();

        warehouse2 = WarehouseBuilder.builder().withId("W2")
            .withLatitude(45.273137).withLongitude(-3.7038).withCountry("ES")
            .withStock(List.of(
                ProductItemBuilder.builder().withProductId("P1")
                    .withSize("M").withQuantity(70).build()
            )).build();

        warehouse3 = WarehouseBuilder.builder().withId("W3")
            .withLatitude(49.4100).withLongitude(-3.735).withCountry("GB")
            .withStock(List.of(
                ProductItemBuilder.builder().withProductId("P2")
                    .withSize("M").withQuantity(30).build()
            )).build();

        when(dataLoaderService.loadProducts()).thenReturn(List.of(product1, product2, product3));
        when(dataLoaderService.loadStores()).thenReturn(List.of(store1, store2));
        when(dataLoaderService.loadWarehouses()).thenReturn(List.of(warehouse1, warehouse2, warehouse3));
    }

    @Test
    void distributeProducts_shouldCreateStockAssignmentsAndUnfulfilledDemands() {
        List<StockAssignment> expectedAssignments = List.of(
            StockAssignmentBuilder.builder().withStoreId("S1").withWarehouseId("W2").withProductId("P1")
                .withSize("M").withQuantity(70).withDistanceKm(540.0).build(),
            StockAssignmentBuilder.builder().withStoreId("S1").withWarehouseId("W1").withProductId("P1")
                .withSize("M").withQuantity(20).withDistanceKm(500.0).build(),
            StockAssignmentBuilder.builder().withStoreId("S1").withWarehouseId("W3").withProductId("P2")
                .withSize("M").withQuantity(10).withDistanceKm(1000.0).build()
        );
        List<UnfulfilledDemand> expectedUnfulfilledDemands = List.of(
            UnfulfilledDemandBuilder.builder().withStoreId("S1").withProductId("P2").withSize("M")
                .withQuantityMissing(35).withReason(UnfulfilledReason.CAPACITY_SHORTAGE).build(),
            UnfulfilledDemandBuilder.builder().withStoreId("S2").withProductId("P3").withSize("M")
                .withQuantityMissing(27).withReason(UnfulfilledReason.STOCK_SHORTAGE).build()
        );

        distributionService.distributeProducts();

        verify(dataLoaderService).loadProducts();
        verify(dataLoaderService).loadStores();
        verify(dataLoaderService).loadWarehouses();

        List<StockAssignment> savedAssignments = stockAssignmentRepository.findAll();
        assertThat(savedAssignments).usingRecursiveComparison()
            .ignoringFields("id")
            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1, "distanceKm")
            .isEqualTo(expectedAssignments);

        List<UnfulfilledDemand> savedUnfulfilledDemands = unfulfilledDemandRepository.findAll();
        assertThat(savedUnfulfilledDemands).usingRecursiveComparison()
            .ignoringFields("id").isEqualTo(expectedUnfulfilledDemands);

        assertThat(storeRepository.findById("S1").orElseThrow())
            .extracting(Store::getRemainingCapacity).isEqualTo(0);
        assertThat(storeRepository.findById("S2").orElseThrow())
            .extracting(Store::getRemainingCapacity).isEqualTo(100);

        assertThat(warehouseRepository.findById("W1").orElseThrow().getStock())
            .extracting(ProductItem::getQuantity).containsExactly(30);
        assertThat(warehouseRepository.findById("W2").orElseThrow().getStock())
            .extracting(ProductItem::getQuantity).containsExactly(0);
        assertThat(warehouseRepository.findById("W3").orElseThrow().getStock())
            .extracting(ProductItem::getQuantity).containsExactly(20);
    }
}