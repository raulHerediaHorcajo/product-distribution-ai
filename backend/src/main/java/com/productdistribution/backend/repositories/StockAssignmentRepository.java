package com.productdistribution.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.productdistribution.backend.entities.StockAssignment;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockAssignmentRepository extends JpaRepository<StockAssignment, UUID>, JpaSpecificationExecutor<StockAssignment> {
    List<StockAssignment> findByStoreId(String storeId);
    List<StockAssignment> findByWarehouseId(String warehouseId);
    List<StockAssignment> findByProductId(String productId);
    @Modifying
    @Query(value = "TRUNCATE TABLE stock_assignments RESTART IDENTITY", nativeQuery = true)
    void truncateAll();
}