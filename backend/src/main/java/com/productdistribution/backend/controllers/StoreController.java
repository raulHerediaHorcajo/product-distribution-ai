package com.productdistribution.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.productdistribution.backend.dtos.StoreDTO;
import com.productdistribution.backend.mappers.StoreMapper;
import com.productdistribution.backend.services.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;
    private final StoreMapper storeMapper;

    @Autowired
    public StoreController(StoreService storeService, StoreMapper storeMapper) {
        this.storeService = storeService;
        this.storeMapper = storeMapper;
    }

    @GetMapping
    public List<StoreDTO> getAllStores() {
        return storeMapper.toDTOList(storeService.getAllStores());
    }

    @GetMapping("/{id}")
    public StoreDTO getStoreById(@PathVariable String id) {
        return storeMapper.toDTO(storeService.getStoreById(id));
    }
}