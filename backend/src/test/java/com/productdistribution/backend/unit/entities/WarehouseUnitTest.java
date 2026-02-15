package com.productdistribution.backend.unit.entities;

import com.productdistribution.backend.entities.ProductItem;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.utils.WarehouseBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class WarehouseUnitTest {

    @Test
    void findStock_shouldReturnProductItem() {
        Warehouse warehouse = WarehouseBuilder.warehouse1();

        Optional<ProductItem> result = warehouse.findStock("P1", "M");

        assertThat(result).isPresent();
        assertThat(result.get().getProductId()).isEqualTo("P1");
        assertThat(result.get().getSize()).isEqualTo("M");
    }

    @Test
    void findStock_whenProductNotFound_shouldReturnEmpty() {
        Warehouse warehouse = WarehouseBuilder.warehouse1();

        Optional<ProductItem> result = warehouse.findStock("P999", "M");

        assertThat(result).isEmpty();
    }

    @Test
    void findStock_whenSizeNotFound_shouldReturnEmpty() {
        Warehouse warehouse = WarehouseBuilder.warehouse1();

        Optional<ProductItem> result = warehouse.findStock("P1", "XL");

        assertThat(result).isEmpty();
    }

    @Test
    void findStock_whenStockIsEmpty_shouldReturnEmpty() {
        Warehouse warehouse = WarehouseBuilder.builder().withStock(List.of()).build();

        Optional<ProductItem> result = warehouse.findStock("P1", "M");

        assertThat(result).isEmpty();
    }

    @Test
    void getStockForProduct_shouldReturnQuantity() {
        Warehouse warehouse = WarehouseBuilder.warehouse1();

        int stock = warehouse.getStockForProduct("P1", "M");

        assertThat(stock).isEqualTo(50);
    }

    @Test
    void getStockForProduct_whenProductNotFound_shouldReturnZero() {
        Warehouse warehouse = WarehouseBuilder.warehouse1();

        int stock = warehouse.getStockForProduct("P999", "M");

        assertThat(stock).isZero();
    }

    @Test
    void getStockForProduct_whenSizeNotFound_shouldReturnZero() {
        Warehouse warehouse = WarehouseBuilder.warehouse1();

        int stock = warehouse.getStockForProduct("P1", "XL");

        assertThat(stock).isZero();
    }

    @Test
    void getStockForProduct_whenStockIsEmpty_shouldReturnZero() {
        Warehouse warehouse = WarehouseBuilder.builder().withStock(List.of()).build();

        int stock = warehouse.getStockForProduct("P1", "M");

        assertThat(stock).isZero();
    }
}