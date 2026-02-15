package com.productdistribution.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.productdistribution.backend.entities.StockAssignment;

import java.util.List;
import java.util.UUID;

@Repository
public interface MetricsRepository extends JpaRepository<StockAssignment, UUID> {

    // ========== GLOBAL METRICS (no filters) ==========

    @Query(value = """
        SELECT 
            COUNT(DISTINCT CONCAT(warehouse_id, '-', store_id)) as total_shipments,
            COALESCE(SUM(quantity), 0) as fulfilled_units,
            COALESCE((SELECT AVG(distance_km) FROM (
                SELECT DISTINCT warehouse_id, store_id, distance_km 
                FROM stock_assignments
            )), 0) as average_distance,
            COALESCE((SELECT SUM(quantity_missing) FROM unfulfilled_demands), 0) as unfulfilled_units
        FROM stock_assignments
        """, nativeQuery = true)
    GlobalMetricsProjection getGlobalMetrics();

    // ========== DETAILED METRICS (with filters) ==========

    @Query(value = """
        SELECT 
            COUNT(DISTINCT CONCAT(sa.warehouse_id, '-', sa.store_id)) as total_shipments,
            COALESCE(SUM(sa.quantity), 0) as total_fulfilled,
            COALESCE((SELECT SUM(quantity_missing) FROM unfulfilled_demands ud 
                WHERE (:warehouseId IS NULL OR ud.store_id IN (
                    SELECT DISTINCT store_id FROM stock_assignments WHERE warehouse_id = :warehouseId
                ))
                AND (:storeId IS NULL OR ud.store_id = :storeId)
                AND (:productId IS NULL OR ud.product_id = :productId)), 0) as total_unfulfilled,
            COALESCE((SELECT SUM(distance_km) FROM (
                SELECT DISTINCT warehouse_id, store_id, distance_km 
                FROM stock_assignments 
                WHERE (:warehouseId IS NULL OR warehouse_id = :warehouseId)
                AND (:storeId IS NULL OR store_id = :storeId)
                AND (:productId IS NULL OR product_id = :productId)
            )), 0) as total_distance,
            COALESCE((SELECT AVG(shipment_total) FROM (
                SELECT SUM(quantity) as shipment_total
                FROM stock_assignments
                WHERE (:warehouseId IS NULL OR warehouse_id = :warehouseId)
                AND (:storeId IS NULL OR store_id = :storeId)
                AND (:productId IS NULL OR product_id = :productId)
                GROUP BY warehouse_id, store_id
            )), 0) as avg_shipment_size,
            COUNT(DISTINCT sa.product_id) as unique_products_distributed,
            (SELECT COUNT(DISTINCT product_id) FROM (
                SELECT product_id FROM stock_assignments sa2
                WHERE (:warehouseId IS NULL OR sa2.warehouse_id = :warehouseId)
                AND (:storeId IS NULL OR sa2.store_id = :storeId)
                AND (:productId IS NULL OR sa2.product_id = :productId)
                UNION
                SELECT product_id FROM unfulfilled_demands ud
                WHERE (:warehouseId IS NULL OR ud.store_id IN (
                    SELECT DISTINCT store_id FROM stock_assignments WHERE warehouse_id = :warehouseId
                ))
                AND (:storeId IS NULL OR ud.store_id = :storeId)
                AND (:productId IS NULL OR ud.product_id = :productId)
            )) as unique_products_requested,
            COUNT(DISTINCT sa.store_id) as served_stores,
            COUNT(DISTINCT CASE WHEN sa.store_id NOT IN (
                SELECT DISTINCT store_id FROM unfulfilled_demands ud
                WHERE (:warehouseId IS NULL OR ud.store_id IN (
                    SELECT DISTINCT store_id FROM stock_assignments WHERE warehouse_id = :warehouseId
                ))
                AND (:storeId IS NULL OR ud.store_id = :storeId)
                AND (:productId IS NULL OR ud.product_id = :productId)
            ) THEN sa.store_id END) as fully_served_stores,
            (SELECT COUNT(DISTINCT store_id) FROM (
                SELECT store_id FROM stock_assignments
                WHERE (:warehouseId IS NULL OR warehouse_id = :warehouseId)
                AND (:storeId IS NULL OR store_id = :storeId)
                AND (:productId IS NULL OR product_id = :productId)
                UNION
                SELECT store_id FROM unfulfilled_demands
                WHERE (:warehouseId IS NULL OR store_id IN (
                    SELECT DISTINCT store_id FROM stock_assignments WHERE warehouse_id = :warehouseId
                ))
                AND (:storeId IS NULL OR store_id = :storeId)
                AND (:productId IS NULL OR product_id = :productId)
            )) as total_stores
        FROM stock_assignments sa
        WHERE (:warehouseId IS NULL OR sa.warehouse_id = :warehouseId)
        AND (:storeId IS NULL OR sa.store_id = :storeId)
        AND (:productId IS NULL OR sa.product_id = :productId)
        """, nativeQuery = true)
    DetailedMetricsProjection getDetailedMetrics(
        @Param("warehouseId") String warehouseId,
        @Param("storeId") String storeId,
        @Param("productId") String productId
    );

    @Query(value = """
        SELECT 
            COALESCE(SUM(quantity_missing), 0) as total_units,
            COALESCE(SUM(CASE WHEN reason = 'STOCK_SHORTAGE' THEN quantity_missing ELSE 0 END), 0) as units_by_stock_shortage,
            COALESCE(SUM(CASE WHEN reason = 'CAPACITY_SHORTAGE' THEN quantity_missing ELSE 0 END), 0) as units_by_capacity_shortage
        FROM unfulfilled_demands
        WHERE (:warehouseId IS NULL OR store_id IN (
            SELECT DISTINCT store_id FROM stock_assignments WHERE warehouse_id = :warehouseId
        ))
        AND (:storeId IS NULL OR store_id = :storeId)
        AND (:productId IS NULL OR product_id = :productId)
        """, nativeQuery = true)
    UnfulfilledDemandProjection getUnfulfilledDemand(
        @Param("warehouseId") String warehouseId,
        @Param("storeId") String storeId,
        @Param("productId") String productId
    );

    @Query(value = """
        SELECT 
            COALESCE(MIN(distance_km), 0) as min_distance,
            COALESCE(MAX(distance_km), 0) as max_distance,
            COALESCE(PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY distance_km), 0) as median_distance
        FROM (
            SELECT DISTINCT warehouse_id, store_id, distance_km
            FROM stock_assignments
            WHERE (:warehouseId IS NULL OR warehouse_id = :warehouseId)
            AND (:storeId IS NULL OR store_id = :storeId)
            AND (:productId IS NULL OR product_id = :productId)
        )
        """, nativeQuery = true)
    DistanceStatsProjection getDistanceStats(
        @Param("warehouseId") String warehouseId,
        @Param("storeId") String storeId,
        @Param("productId") String productId
    );

    @Query(value = """
        SELECT 
            wu.warehouse_id as warehouseId,
            wu.total_units as totalUnits,
            wu.percentage as percentage,
            COALESCE(wd.avg_distance, 0) as avgDistance
        FROM (
            SELECT 
                warehouse_id,
                SUM(quantity) as total_units,
                (SUM(quantity) * 100.0 / (SELECT SUM(quantity) FROM stock_assignments 
                    WHERE (:warehouseId IS NULL OR warehouse_id = :warehouseId)
                    AND (:storeId IS NULL OR store_id = :storeId)
                    AND (:productId IS NULL OR product_id = :productId))) as percentage
            FROM stock_assignments
            WHERE (:warehouseId IS NULL OR warehouse_id = :warehouseId)
            AND (:storeId IS NULL OR store_id = :storeId)
            AND (:productId IS NULL OR product_id = :productId)
            GROUP BY warehouse_id
        ) wu
        LEFT JOIN (
            SELECT 
                warehouse_id,
                AVG(distance_km) as avg_distance
            FROM (
                SELECT DISTINCT warehouse_id, store_id, distance_km
                FROM stock_assignments
                WHERE (:warehouseId IS NULL OR warehouse_id = :warehouseId)
                AND (:storeId IS NULL OR store_id = :storeId)
                AND (:productId IS NULL OR product_id = :productId)
            )
            GROUP BY warehouse_id
        ) wd ON wu.warehouse_id = wd.warehouse_id
        ORDER BY wu.total_units DESC
        LIMIT 10
        """, nativeQuery = true)
    List<WarehouseMetricsProjection> getWarehouseMetrics(
        @Param("warehouseId") String warehouseId,
        @Param("storeId") String storeId,
        @Param("productId") String productId
    );

    @Query(value = """
        SELECT 
            product_id as productId,
            SUM(quantity) as totalQuantity
        FROM stock_assignments
        WHERE (:warehouseId IS NULL OR warehouse_id = :warehouseId)
        AND (:storeId IS NULL OR store_id = :storeId)
        AND (:productId IS NULL OR product_id = :productId)
        GROUP BY product_id
        ORDER BY totalQuantity DESC
        LIMIT 5
        """, nativeQuery = true)
    List<TopProductProjection> getTopProducts(
        @Param("warehouseId") String warehouseId,
        @Param("storeId") String storeId,
        @Param("productId") String productId
    );

    @Query(value = """
        SELECT 
            COALESCE(SUM(s.max_stock_capacity - s.remaining_capacity), 0) as used_capacity,
            COALESCE(SUM(s.max_stock_capacity), 0) as total_capacity,
            COUNT(*) as total_stores,
            COUNT(CASE WHEN s.remaining_capacity = 0 THEN 1 END) as stores_at_capacity
        FROM stores s
        WHERE s.id IN (
            SELECT DISTINCT store_id FROM stock_assignments
            WHERE (:warehouseId IS NULL OR warehouse_id = :warehouseId)
            AND (:storeId IS NULL OR store_id = :storeId)
            AND (:productId IS NULL OR product_id = :productId)
            UNION
            SELECT DISTINCT store_id FROM unfulfilled_demands
            WHERE (:warehouseId IS NULL OR store_id IN (
                SELECT DISTINCT store_id FROM stock_assignments WHERE warehouse_id = :warehouseId
            ))
            AND (:storeId IS NULL OR store_id = :storeId)
            AND (:productId IS NULL OR product_id = :productId)
        )
        """, nativeQuery = true)
    CapacityUtilizationProjection getCapacityUtilization(
        @Param("warehouseId") String warehouseId,
        @Param("storeId") String storeId,
        @Param("productId") String productId
    );

    // ========== PROJECTION INTERFACES ==========

    interface GlobalMetricsProjection {
        Long getTotalShipments();
        Long getFulfilledUnits();
        Long getUnfulfilledUnits();
        Double getAverageDistance();
    }

    interface DetailedMetricsProjection {
        Long getTotalShipments();
        Long getTotalFulfilled();
        Long getTotalUnfulfilled();
        Double getTotalDistance();
        Double getAvgShipmentSize();
        Integer getUniqueProductsDistributed();
        Integer getUniqueProductsRequested();
        Integer getServedStores();
        Integer getFullyServedStores();
        Integer getTotalStores();
    }

    interface UnfulfilledDemandProjection {
        Long getTotalUnits();
        Long getUnitsByStockShortage();
        Long getUnitsByCapacityShortage();
    }

    interface DistanceStatsProjection {
        Double getMinDistance();
        Double getMaxDistance();
        Double getMedianDistance();
    }

    interface WarehouseMetricsProjection {
        String getWarehouseId();
        Long getTotalUnits();
        Double getPercentage();
        Double getAvgDistance();
    }

    interface TopProductProjection {
        String getProductId();
        Long getTotalQuantity();
    }

    interface CapacityUtilizationProjection {
        Long getUsedCapacity();
        Long getTotalCapacity();
        Integer getTotalStores();
        Integer getStoresAtCapacity();
    }
}