package com.productdistribution.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.productdistribution.backend.dtos.WarehouseDTO;
import com.productdistribution.backend.mappers.WarehouseMapper;
import com.productdistribution.backend.services.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final WarehouseMapper warehouseMapper;

    @Autowired
    public WarehouseController(WarehouseService warehouseService, WarehouseMapper warehouseMapper) {
        this.warehouseService = warehouseService;
        this.warehouseMapper = warehouseMapper;
    }

    @GetMapping
    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseMapper.toDTOList(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public WarehouseDTO getWarehouseById(@PathVariable String id) {
        return warehouseMapper.toDTO(warehouseService.getWarehouseById(id));
    }
}