package com.productdistribution.backend.repositories.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import com.productdistribution.backend.entities.StockAssignment;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;

public final class StockAssignmentSpecifications {

    private StockAssignmentSpecifications() {
        // Utility class - prevent instantiation
    }

    public static Specification<StockAssignment> withCriteria(StockAssignmentCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (criteria.getStoreId() != null && !criteria.getStoreId().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("storeId"), criteria.getStoreId()));
            }

            if (criteria.getWarehouseId() != null && !criteria.getWarehouseId().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("warehouseId"), criteria.getWarehouseId()));
            }

            if (criteria.getProductId() != null && !criteria.getProductId().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("productId"), criteria.getProductId()));
            }

            return predicate;
        };
    }
}