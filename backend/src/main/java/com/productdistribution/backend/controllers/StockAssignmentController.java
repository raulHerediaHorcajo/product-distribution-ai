package com.productdistribution.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.productdistribution.backend.dtos.StockAssignmentDTO;
import com.productdistribution.backend.mappers.StockAssignmentMapper;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.services.DistributionService;
import com.productdistribution.backend.services.StockAssignmentService;

import java.util.List;

@RestController
@RequestMapping("/api/stock-assignments")
public class StockAssignmentController {

    private final DistributionService distributionService;
    private final StockAssignmentService stockAssignmentService;
    private final StockAssignmentMapper stockAssignmentMapper;

    @Autowired
    public StockAssignmentController(DistributionService distributionService, StockAssignmentService stockAssignmentService, StockAssignmentMapper stockAssignmentMapper) {
        this.distributionService = distributionService;
        this.stockAssignmentService = stockAssignmentService;
        this.stockAssignmentMapper = stockAssignmentMapper;
    }

    @PostMapping("/distribute")
    public List<StockAssignmentDTO> distributeProducts() {
        return stockAssignmentMapper.toDTOList(distributionService.distributeProducts());
    }

    @GetMapping
    public List<StockAssignmentDTO> getAssignments(@Valid StockAssignmentCriteria criteria) {
        return stockAssignmentMapper.toDTOList(stockAssignmentService.getAssignments(criteria));
    }

    @GetMapping("/stores/{storeId}")
    public List<StockAssignmentDTO> getAssignmentsByStore(@PathVariable String storeId) {
        return stockAssignmentMapper.toDTOList(stockAssignmentService.getByStoreId(storeId));
    }

    @GetMapping("/products/{productId}")
    public List<StockAssignmentDTO> getAssignmentsByProduct(@PathVariable String productId) {
        return stockAssignmentMapper.toDTOList(stockAssignmentService.getByProductId(productId));
    }

    @GetMapping("/warehouses/{warehouseId}")
    public List<StockAssignmentDTO> getAssignmentsByWarehouse(@PathVariable String warehouseId) {
        return stockAssignmentMapper.toDTOList(stockAssignmentService.getByWarehouseId(warehouseId));
    }
}