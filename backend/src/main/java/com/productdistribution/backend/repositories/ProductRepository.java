package com.productdistribution.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.productdistribution.backend.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE products RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncateAll();
}