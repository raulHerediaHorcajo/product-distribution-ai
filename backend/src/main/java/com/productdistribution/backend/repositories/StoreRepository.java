package com.productdistribution.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.productdistribution.backend.entities.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE stores RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncateAll();
}