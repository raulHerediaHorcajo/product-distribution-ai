package com.productdistribution.backend.unit.mappers;

import com.productdistribution.backend.dtos.StoreDTO;
import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.mappers.StoreMapper;
import com.productdistribution.backend.utils.ProductItemBuilder;
import com.productdistribution.backend.utils.ProductItemDTOBuilder;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.StoreDTOBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StoreMapperUnitTest {

    private final StoreMapper storeMapper = Mappers.getMapper(StoreMapper.class);

    @Test
    void toDTO_shouldReturnDTO() {
        Store store = StoreBuilder.store1();
        StoreDTO expected = StoreDTOBuilder.storeDTO1();

        StoreDTO dto = storeMapper.toDTO(store);

        assertThat(dto).isEqualTo(expected);
    }

    @Test
    void toDTO_whenNull_shouldReturnNull() {
        Store store = null;

        StoreDTO dto = storeMapper.toDTO(store);

        assertThat(dto).isNull();
    }

    @Test
    void toDTO_whenDemandContainsNull_shouldReturnDTOWithNullInDemand() {
        Store store = StoreBuilder.builder()
            .withDemand(Arrays.asList(ProductItemBuilder.productItem1(), null)).build();
        StoreDTO expected = StoreDTOBuilder.builder()
            .withDemand(Arrays.asList(ProductItemDTOBuilder.productItemDTO1(), null)).build();  

        StoreDTO dto = storeMapper.toDTO(store);

        assertThat(dto).isEqualTo(expected);
    }

    @Test
    void toDTO_whenDemandIsNull_shouldReturnDTOWithNullDemand() {
        Store store = StoreBuilder.builder().withDemand(null).build();
        StoreDTO expected = StoreDTOBuilder.builder().withDemand(null).build();

        StoreDTO dto = storeMapper.toDTO(store);

        assertThat(dto).isEqualTo(expected);
    }

    @Test
    void toEntity_shouldReturnEntity() {
        StoreDTO dto = StoreDTOBuilder.storeDTO1();
        Store expected = StoreBuilder.store1();

        Store store = storeMapper.toEntity(dto);

        assertThat(store).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toEntity_whenNull_shouldReturnNull() {
        StoreDTO dto = null;

        Store store = storeMapper.toEntity(dto);

        assertThat(store).isNull();
    }

    @Test
    void toEntity_whenDemandContainsNull_shouldReturnEntityWithNullInDemand() {
        StoreDTO dto = StoreDTOBuilder.builder()
            .withDemand(Arrays.asList(ProductItemDTOBuilder.productItemDTO1(), null)).build();
        Store expected = StoreBuilder.builder()
            .withDemand(Arrays.asList(ProductItemBuilder.productItem1(), null)).build();

        Store store = storeMapper.toEntity(dto);

        assertThat(store).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toEntity_whenDemandIsNull_shouldReturnEntityWithNullDemand() {
        StoreDTO dto = StoreDTOBuilder.builder().withDemand(null).build();
        Store expected = StoreBuilder.builder().withDemand(null).build();

        Store store = storeMapper.toEntity(dto);

        assertThat(store).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toDTOList_shouldReturnDTOList() {
        List<Store> stores = List.of(
            StoreBuilder.store1(),
            StoreBuilder.store2()
        );
        StoreDTO expected1 = StoreDTOBuilder.storeDTO1();
        StoreDTO expected2 = StoreDTOBuilder.storeDTO2();

        List<StoreDTO> dtos = storeMapper.toDTOList(stores);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0)).isEqualTo(expected1);
        assertThat(dtos.get(1)).isEqualTo(expected2);
    }

    @Test
    void toDTOList_whenNull_shouldReturnNull() {
        List<Store> stores = null;

        List<StoreDTO> dtos = storeMapper.toDTOList(stores);

        assertThat(dtos).isNull();
    }

    @Test
    void toDTOList_whenEmptyList_shouldReturnEmptyList() {
        List<Store> stores = List.of();

        List<StoreDTO> dtos = storeMapper.toDTOList(stores);

        assertThat(dtos).isEmpty();
    }

    @Test
    void toEntityList_shouldReturnEntityList() {
        List<StoreDTO> dtos = List.of(
            StoreDTOBuilder.storeDTO1(),
            StoreDTOBuilder.storeDTO2()
        );
        Store expected1 = StoreBuilder.store1();
        Store expected2 = StoreBuilder.store2();

        List<Store> stores = storeMapper.toEntityList(dtos);

        assertThat(stores).hasSize(2);
        assertThat(stores.get(0)).usingRecursiveComparison().isEqualTo(expected1);
        assertThat(stores.get(1)).usingRecursiveComparison().isEqualTo(expected2);
    }

    @Test
    void toEntityList_whenNull_shouldReturnNull() {
        List<StoreDTO> dtos = null;

        List<Store> stores = storeMapper.toEntityList(dtos);

        assertThat(stores).isNull();
    }

    @Test
    void toEntityList_whenEmptyList_shouldReturnEmptyList() {
        List<StoreDTO> dtos = List.of();

        List<Store> stores = storeMapper.toEntityList(dtos);

        assertThat(stores).isEmpty();
    }
}