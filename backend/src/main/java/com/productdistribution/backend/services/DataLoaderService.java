package com.productdistribution.backend.services;

import java.util.List;

import com.productdistribution.backend.entities.Product;
import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.entities.Warehouse;

public interface DataLoaderService {
    List<Product> loadProducts();
    List<Store> loadStores();
    List<Warehouse> loadWarehouses();
}