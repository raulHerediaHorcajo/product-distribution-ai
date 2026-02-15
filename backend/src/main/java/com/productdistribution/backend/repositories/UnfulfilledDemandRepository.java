package com.productdistribution.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.productdistribution.backend.entities.UnfulfilledDemand;

import java.util.UUID;

@Repository
public interface UnfulfilledDemandRepository extends JpaRepository<UnfulfilledDemand, UUID> {
    @Modifying
    @Query(value = "TRUNCATE TABLE unfulfilled_demands RESTART IDENTITY", nativeQuery = true)
    void truncateAll();
}