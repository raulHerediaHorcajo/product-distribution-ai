package com.productdistribution.backend.unit.mappers;

import com.productdistribution.backend.dtos.WarehouseDTO;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.mappers.WarehouseMapper;
import com.productdistribution.backend.utils.ProductItemBuilder;
import com.productdistribution.backend.utils.ProductItemDTOBuilder;
import com.productdistribution.backend.utils.WarehouseBuilder;
import com.productdistribution.backend.utils.WarehouseDTOBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WarehouseMapperUnitTest {

    private final WarehouseMapper warehouseMapper = Mappers.getMapper(WarehouseMapper.class);

    @Test
    void toDTO_shouldReturnDTO() {
        Warehouse warehouse = WarehouseBuilder.warehouse1();
        WarehouseDTO expected = WarehouseDTOBuilder.warehouseDTO1();

        WarehouseDTO dto = warehouseMapper.toDTO(warehouse);

        assertThat(dto).isEqualTo(expected);
    }

    @Test
    void toDTO_whenNull_shouldReturnNull() {
        Warehouse warehouse = null;

        WarehouseDTO dto = warehouseMapper.toDTO(warehouse);

        assertThat(dto).isNull();
    }

    @Test
    void toDTO_whenStockContainsNull_shouldReturnDTOWithNullInStock() {
        Warehouse warehouse = WarehouseBuilder.builder()
            .withStock(Arrays.asList(ProductItemBuilder.productItem1(), null)).build();
        WarehouseDTO expected = WarehouseDTOBuilder.builder()
            .withStock(Arrays.asList(ProductItemDTOBuilder.productItemDTO1(), null)).build();

        WarehouseDTO dto = warehouseMapper.toDTO(warehouse);

        assertThat(dto).isEqualTo(expected);
    }

    @Test
    void toDTO_whenStockIsNull_shouldReturnDTOWithNullStock() {
        Warehouse warehouse = WarehouseBuilder.builder().withStock(null).build();
        WarehouseDTO expected = WarehouseDTOBuilder.builder().withStock(null).build();

        WarehouseDTO dto = warehouseMapper.toDTO(warehouse);

        assertThat(dto).isEqualTo(expected);
    }

    @Test
    void toEntity_shouldReturnEntity() {
        WarehouseDTO dto = WarehouseDTOBuilder.warehouseDTO1();
        Warehouse expected = WarehouseBuilder.warehouse1();

        Warehouse warehouse = warehouseMapper.toEntity(dto);

        assertThat(warehouse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toEntity_whenNull_shouldReturnNull() {
        WarehouseDTO dto = null;

        Warehouse warehouse = warehouseMapper.toEntity(dto);

        assertThat(warehouse).isNull();
    }

    @Test
    void toEntity_whenStockContainsNull_shouldReturnEntityWithNullInStock() {
        WarehouseDTO dto = WarehouseDTOBuilder.builder()
            .withStock(Arrays.asList(ProductItemDTOBuilder.productItemDTO1(), null)).build();
        Warehouse expected = WarehouseBuilder.builder()
            .withStock(Arrays.asList(ProductItemBuilder.productItem1(), null)).build();

        Warehouse warehouse = warehouseMapper.toEntity(dto);

        assertThat(warehouse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toEntity_whenStockIsNull_shouldReturnEntityWithNullStock() {
        WarehouseDTO dto = WarehouseDTOBuilder.builder().withStock(null).build();
        Warehouse expected = WarehouseBuilder.builder().withStock(null).build();

        Warehouse warehouse = warehouseMapper.toEntity(dto);

        assertThat(warehouse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toDTOList_shouldReturnDTOList() {
        List<Warehouse> warehouses = List.of(
            WarehouseBuilder.warehouse1(),
            WarehouseBuilder.warehouse2()
        );
        WarehouseDTO expected1 = WarehouseDTOBuilder.warehouseDTO1();
        WarehouseDTO expected2 = WarehouseDTOBuilder.warehouseDTO2();

        List<WarehouseDTO> dtos = warehouseMapper.toDTOList(warehouses);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0)).isEqualTo(expected1);
        assertThat(dtos.get(1)).isEqualTo(expected2);
    }

    @Test
    void toDTOList_whenNull_shouldReturnNull() {
        List<Warehouse> warehouses = null;

        List<WarehouseDTO> dtos = warehouseMapper.toDTOList(warehouses);

        assertThat(dtos).isNull();
    }

    @Test
    void toDTOList_whenEmptyList_shouldReturnEmptyList() {
        List<Warehouse> warehouses = List.of();

        List<WarehouseDTO> dtos = warehouseMapper.toDTOList(warehouses);

        assertThat(dtos).isEmpty();
    }

    @Test
    void toEntityList_shouldReturnEntityList() {
        List<WarehouseDTO> dtos = List.of(
            WarehouseDTOBuilder.warehouseDTO1(),
            WarehouseDTOBuilder.warehouseDTO2()
        );
        Warehouse expected1 = WarehouseBuilder.warehouse1();
        Warehouse expected2 = WarehouseBuilder.warehouse2();

        List<Warehouse> warehouses = warehouseMapper.toEntityList(dtos);

        assertThat(warehouses).hasSize(2);
        assertThat(warehouses.get(0)).usingRecursiveComparison().isEqualTo(expected1);
        assertThat(warehouses.get(1)).usingRecursiveComparison().isEqualTo(expected2);
    }

    @Test
    void toEntityList_whenNull_shouldReturnNull() {
        List<WarehouseDTO> dtos = null;

        List<Warehouse> warehouses = warehouseMapper.toEntityList(dtos);

        assertThat(warehouses).isNull();
    }

    @Test
    void toEntityList_whenEmptyList_shouldReturnEmptyList() {
        List<WarehouseDTO> dtos = List.of();

        List<Warehouse> warehouses = warehouseMapper.toEntityList(dtos);

        assertThat(warehouses).isEmpty();
    }
}