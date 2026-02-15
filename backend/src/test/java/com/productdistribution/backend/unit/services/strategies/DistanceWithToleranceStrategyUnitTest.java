package com.productdistribution.backend.unit.services.strategies;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.services.GeoDistanceService;
import com.productdistribution.backend.services.strategies.DistanceWithToleranceStrategy;
import com.productdistribution.backend.services.strategies.WarehouseWithDistance;
import com.productdistribution.backend.utils.StoreBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistanceWithToleranceStrategyUnitTest {

    @Mock
    private GeoDistanceService geoDistanceService;

    @InjectMocks
    private DistanceWithToleranceStrategy distanceWithToleranceStrategy;

    @Test
    void selectWarehouses_whenDistancesOutsideTolerance_shouldOrderByDistance() {
        Store store = StoreBuilder.store1();
        Warehouse warehouse1 = mock(Warehouse.class);
        Warehouse warehouse2 = mock(Warehouse.class);
        List<Warehouse> warehouses = List.of(warehouse1, warehouse2);
        String productId = "P1";
        String size = "M";

        when(warehouse1.getLatitude()).thenReturn(48.8566);
        when(warehouse1.getLongitude()).thenReturn(2.3522);
        when(warehouse2.getLatitude()).thenReturn(41.3851);
        when(warehouse2.getLongitude()).thenReturn(2.1734);

        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse1.getLatitude(), warehouse1.getLongitude()))
                .thenReturn(100.0);
        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse2.getLatitude(), warehouse2.getLongitude()))
                .thenReturn(80.0);

        List<WarehouseWithDistance> result = distanceWithToleranceStrategy.selectWarehouses(store, warehouses, productId, size);

        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse1.getLatitude(), warehouse1.getLongitude());
        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse2.getLatitude(), warehouse2.getLongitude());
        verify(warehouse1, never()).getStockForProduct(anyString(), anyString());
        verify(warehouse2, never()).getStockForProduct(anyString(), anyString());
        assertThat(result).hasSize(2);
        assertThat(result.get(0).warehouse()).isEqualTo(warehouse2);
        assertThat(result.get(0).distanceKm()).isEqualTo(80.0);
        assertThat(result.get(1).warehouse()).isEqualTo(warehouse1);
        assertThat(result.get(1).distanceKm()).isEqualTo(100.0);
    }

    @Test
    void selectWarehouses_whenDistancesWithinTolerance_shouldOrderByStock() {
        Store store = StoreBuilder.store1();
        Warehouse warehouse1 = mock(Warehouse.class);
        Warehouse warehouse2 = mock(Warehouse.class);
        List<Warehouse> warehouses = List.of(warehouse1, warehouse2);
        String productId = "P1";
        String size = "M";

        when(warehouse1.getLatitude()).thenReturn(48.8566);
        when(warehouse1.getLongitude()).thenReturn(2.3522);
        when(warehouse2.getLatitude()).thenReturn(48.8600);
        when(warehouse2.getLongitude()).thenReturn(2.3600);

        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse1.getLatitude(), warehouse1.getLongitude()))
                .thenReturn(100.0);
        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse2.getLatitude(), warehouse2.getLongitude()))
                .thenReturn(95.0);
        when(warehouse1.getStockForProduct(productId, size)).thenReturn(100);
        when(warehouse2.getStockForProduct(productId, size)).thenReturn(50);

        List<WarehouseWithDistance> result = distanceWithToleranceStrategy.selectWarehouses(store, warehouses, productId, size);

        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse1.getLatitude(), warehouse1.getLongitude());
        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse2.getLatitude(), warehouse2.getLongitude());
        verify(warehouse1).getStockForProduct(productId, size);
        verify(warehouse2).getStockForProduct(productId, size);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).warehouse()).isEqualTo(warehouse1);
        assertThat(result.get(0).distanceKm()).isEqualTo(100.0);
        assertThat(result.get(1).warehouse()).isEqualTo(warehouse2);
        assertThat(result.get(1).distanceKm()).isEqualTo(95.0);
    }

    @Test
    void selectWarehouses_whenEmptyWarehouseList_shouldReturnEmptyList() {
        Store store = StoreBuilder.store1();
        List<Warehouse> warehouses = List.of();
        String productId = "P1";
        String size = "M";

        List<WarehouseWithDistance> result = distanceWithToleranceStrategy.selectWarehouses(store, warehouses, productId, size);

        verify(geoDistanceService, never()).calculateHaversineDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        assertThat(result).isEmpty();
    }

    @Test
    void selectWarehouses_whenSingleWarehouse_shouldReturnSingleElementList() {
        Store store = StoreBuilder.store1();
        Warehouse warehouse = mock(Warehouse.class);
        List<Warehouse> warehouses = List.of(warehouse);
        String productId = "P1";
        String size = "M";

        when(warehouse.getLatitude()).thenReturn(48.8566);
        when(warehouse.getLongitude()).thenReturn(2.3522);
        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse.getLatitude(), warehouse.getLongitude()))
                .thenReturn(75.5);

        List<WarehouseWithDistance> result = distanceWithToleranceStrategy.selectWarehouses(store, warehouses, productId, size);

        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse.getLatitude(), warehouse.getLongitude());
        verify(warehouse, never()).getStockForProduct(anyString(), anyString());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).warehouse()).isEqualTo(warehouse);
        assertThat(result.get(0).distanceKm()).isEqualTo(75.5);
    }

    @Test
    void selectWarehouses_whenWarehouseHasNoStock_shouldPlaceWarehouseWithNoStockLast() {
        Store store = StoreBuilder.store1();
        Warehouse warehouse1 = mock(Warehouse.class);
        Warehouse warehouse2 = mock(Warehouse.class);
        List<Warehouse> warehouses = List.of(warehouse1, warehouse2);
        String productId = "P1";
        String size = "M";

        when(warehouse1.getLatitude()).thenReturn(48.8566);
        when(warehouse1.getLongitude()).thenReturn(2.3522);
        when(warehouse2.getLatitude()).thenReturn(48.8600);
        when(warehouse2.getLongitude()).thenReturn(2.3600);

        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse1.getLatitude(), warehouse1.getLongitude()))
                .thenReturn(100.0);
        when(geoDistanceService.calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse2.getLatitude(), warehouse2.getLongitude()))
                .thenReturn(95.0);
        when(warehouse1.getStockForProduct(productId, size)).thenReturn(0);
        when(warehouse2.getStockForProduct(productId, size)).thenReturn(50);

        List<WarehouseWithDistance> result = distanceWithToleranceStrategy.selectWarehouses(store, warehouses, productId, size);

        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse1.getLatitude(), warehouse1.getLongitude());
        verify(geoDistanceService).calculateHaversineDistance(
                store.getLatitude(), store.getLongitude(),
                warehouse2.getLatitude(), warehouse2.getLongitude());
        verify(warehouse1).getStockForProduct(productId, size);
        verify(warehouse2).getStockForProduct(productId, size);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).warehouse()).isEqualTo(warehouse2);
        assertThat(result.get(0).distanceKm()).isEqualTo(95.0);
        assertThat(result.get(1).warehouse()).isEqualTo(warehouse1);
        assertThat(result.get(1).distanceKm()).isEqualTo(100.0);
    }
}