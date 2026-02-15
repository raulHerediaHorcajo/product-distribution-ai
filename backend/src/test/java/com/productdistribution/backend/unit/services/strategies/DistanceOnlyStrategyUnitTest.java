package com.productdistribution.backend.unit.services.strategies;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.services.GeoDistanceService;
import com.productdistribution.backend.services.strategies.DistanceOnlyStrategy;
import com.productdistribution.backend.services.strategies.WarehouseWithDistance;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.WarehouseBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistanceOnlyStrategyUnitTest {

    @Mock
    private GeoDistanceService geoDistanceService;

    @InjectMocks
    private DistanceOnlyStrategy distanceOnlyStrategy;

    @Test
    void selectWarehouses_shouldReturnWarehousesOrderedByDistance() {
        Store store = StoreBuilder.store1();
        Warehouse warehouse1 = WarehouseBuilder.warehouse1();
        Warehouse warehouse2 = WarehouseBuilder.warehouse2();
        List<Warehouse> warehouses = List.of(warehouse1, warehouse2);
        String productId = "P1";
        String size = "M";

        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse1.getLatitude(), warehouse1.getLongitude()))
                .thenReturn(100.0);
        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse2.getLatitude(), warehouse2.getLongitude()))
                .thenReturn(50.0);

        List<WarehouseWithDistance> result = distanceOnlyStrategy.selectWarehouses(store, warehouses, productId, size);

        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse1.getLatitude(), warehouse1.getLongitude());
        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse2.getLatitude(), warehouse2.getLongitude());
        assertThat(result).hasSize(2);
        assertThat(result.get(0).warehouse()).isEqualTo(warehouse2);
        assertThat(result.get(0).distanceKm()).isEqualTo(50.0);
        assertThat(result.get(1).warehouse()).isEqualTo(warehouse1);
        assertThat(result.get(1).distanceKm()).isEqualTo(100.0);
    }

    @Test
    void selectWarehouses_whenEmptyWarehouseList_shouldReturnEmptyList() {
        Store store = StoreBuilder.store1();
        List<Warehouse> warehouses = List.of();
        String productId = "P1";
        String size = "M";

        List<WarehouseWithDistance> result = distanceOnlyStrategy.selectWarehouses(store, warehouses, productId, size);

        verify(geoDistanceService, never()).calculateHaversineDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        assertThat(result).isEmpty();
    }

    @Test
    void selectWarehouses_whenSingleWarehouse_shouldReturnSingleElementList() {
        Store store = StoreBuilder.store1();
        Warehouse warehouse = WarehouseBuilder.warehouse1();
        List<Warehouse> warehouses = List.of(warehouse);
        String productId = "P1";
        String size = "M";

        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse.getLatitude(), warehouse.getLongitude()))
                .thenReturn(75.5);

        List<WarehouseWithDistance> result = distanceOnlyStrategy.selectWarehouses(store, warehouses, productId, size);

        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse.getLatitude(), warehouse.getLongitude());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).warehouse()).isEqualTo(warehouse);
        assertThat(result.get(0).distanceKm()).isEqualTo(75.5);
    }
}