package com.productdistribution.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.productdistribution.backend.entities.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE warehouses RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncateAll();
}