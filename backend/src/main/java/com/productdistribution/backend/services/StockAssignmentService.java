package com.productdistribution.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.productdistribution.backend.entities.StockAssignment;
import com.productdistribution.backend.repositories.StockAssignmentRepository;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.repositories.specification.StockAssignmentSpecifications;

import java.util.List;

@Service
public class StockAssignmentService {

    private final StockAssignmentRepository stockAssignmentRepository;

    @Autowired
    public StockAssignmentService(StockAssignmentRepository stockAssignmentRepository) {
        this.stockAssignmentRepository = stockAssignmentRepository;
    }

    public void add(StockAssignment stockAssignment) {
        stockAssignmentRepository.save(stockAssignment);
    }

    public void addAll(List<StockAssignment> stockAssignments) {
        stockAssignmentRepository.saveAll(stockAssignments);
    }

    public List<StockAssignment> getAllStockAssignments() {
        return stockAssignmentRepository.findAll();
    }

    public List<StockAssignment> getAssignments(StockAssignmentCriteria criteria) {
        Specification<StockAssignment> spec = StockAssignmentSpecifications.withCriteria(criteria);
        return stockAssignmentRepository.findAll(spec);
    }

    public List<StockAssignment> getByStoreId(String storeId) {
        return stockAssignmentRepository.findByStoreId(storeId);
    }

    public List<StockAssignment> getByProductId(String productId) {
        return stockAssignmentRepository.findByProductId(productId);
    }

    public List<StockAssignment> getByWarehouseId(String warehouseId) {
        return stockAssignmentRepository.findByWarehouseId(warehouseId);
    }

    public void deleteAll() {
        stockAssignmentRepository.truncateAll();
    }
}