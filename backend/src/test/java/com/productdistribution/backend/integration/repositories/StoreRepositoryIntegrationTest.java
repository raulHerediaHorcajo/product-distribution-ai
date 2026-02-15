package com.productdistribution.backend.integration.repositories;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.utils.ProductItemBuilder;
import com.productdistribution.backend.utils.StoreBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StoreRepositoryIntegrationTest {

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void save_shouldPersistStore() {
        Store store = StoreBuilder.store1WithoutProductItemIds();

        Store saved = storeRepository.save(store);

        Optional<Store> found = storeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison()
            .ignoringFields("demand.id").isEqualTo(store);
    }

    @Test
    void save_shouldPersistDemandList() {
        Store store = StoreBuilder.builder()
                .withDemand(List.of(ProductItemBuilder.productItem1WithoutId())).build();

        Store saved = storeRepository.save(store);

        Optional<Store> found = storeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDemand()).hasSize(1);
        assertThat(found.get().getDemand())
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
            .containsExactly(ProductItemBuilder.productItem1WithoutId());
    }

    @Test
    void save_whenDemandIsNull_shouldPersistStoreWithNullDemand() {
        Store store = StoreBuilder.builder()
                .withDemand(null).build();

        Store saved = storeRepository.save(store);

        Optional<Store> found = storeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDemand()).isNull();
    }

    @Test
    void findById_whenStoreExists_shouldReturnStore() {
        Store store = StoreBuilder.store2WithoutProductItemIds();
        Store saved = storeRepository.save(store);

        Optional<Store> found = storeRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison()
            .ignoringFields("demand.id").isEqualTo(store);
    }

    @Test
    void findById_whenStoreNotExists_shouldReturnEmpty() {
        Optional<Store> found = storeRepository.findById("NON_EXISTENT");

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_whenStoresExist_shouldReturnAllStores() {
        List<Store> stores = List.of(StoreBuilder.store1WithoutProductItemIds(), StoreBuilder.store2WithoutProductItemIds());
        storeRepository.saveAll(stores);

        List<Store> found = storeRepository.findAll();

        assertThat(found).usingRecursiveComparison()
            .ignoringFields("demand.id").isEqualTo(stores);
    }

    @Test
    void findAllByOrderByIdAsc_whenStoresAreUnordered_shouldReturnAllSorted() {
        List<Store> stores = List.of(StoreBuilder.store2WithoutProductItemIds(), StoreBuilder.store1WithoutProductItemIds());
        storeRepository.saveAll(stores);
        List<Store> expectedStores = List.of(StoreBuilder.store1WithoutProductItemIds(), StoreBuilder.store2WithoutProductItemIds());

        List<Store> found = storeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        
        assertThat(found).usingRecursiveComparison()
            .ignoringFields("demand.id").isEqualTo(expectedStores);
    }

    @Test
    void findAll_whenNoStores_shouldReturnEmptyList() {
        List<Store> found = storeRepository.findAll();

        assertThat(found).isEmpty();
    }

    @Test
    void saveAll_shouldPersistMultipleStores() {
        List<Store> stores = List.of(StoreBuilder.store2WithoutProductItemIds(), StoreBuilder.store1WithoutProductItemIds());

        storeRepository.saveAll(stores);

        List<Store> found = storeRepository.findAll();
        assertThat(found).usingRecursiveComparison()
            .ignoringFields("demand.id").isEqualTo(stores);
    }

    @Test
    void truncateAll_shouldRemoveAllStores() {
        List<Store> stores = List.of(StoreBuilder.store1WithoutProductItemIds(), StoreBuilder.store2WithoutProductItemIds());
        storeRepository.saveAll(stores);

        storeRepository.truncateAll();

        List<Store> found = storeRepository.findAll();
        assertThat(found).isEmpty();
    }
}