package com.productdistribution.backend.integration.repositories;

import com.productdistribution.backend.entities.UnfulfilledDemand;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.repositories.UnfulfilledDemandRepository;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.UnfulfilledDemandBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UnfulfilledDemandRepositoryIntegrationTest {

    @Autowired
    private UnfulfilledDemandRepository unfulfilledDemandRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void save_shouldPersistUnfulfilledDemand() {
        storeRepository.save(StoreBuilder.store1WithoutProductItemIds());

        UnfulfilledDemand unfulfilledDemand = UnfulfilledDemandBuilder.unfulfilledDemand1WithoutId();

        UnfulfilledDemand saved = unfulfilledDemandRepository.saveAndFlush(unfulfilledDemand);

        Optional<UnfulfilledDemand> found = unfulfilledDemandRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(unfulfilledDemand);
    }

    @Test
    void findById_whenUnfulfilledDemandExists_shouldReturnUnfulfilledDemand() {
        storeRepository.save(StoreBuilder.store2WithoutProductItemIds());

        UnfulfilledDemand unfulfilledDemand = UnfulfilledDemandBuilder.unfulfilledDemand2WithoutId();

        UnfulfilledDemand saved = unfulfilledDemandRepository.saveAndFlush(unfulfilledDemand);

        Optional<UnfulfilledDemand> found = unfulfilledDemandRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(unfulfilledDemand);
    }

    @Test
    void findById_whenUnfulfilledDemandNotExists_shouldReturnEmpty() {
        Optional<UnfulfilledDemand> found = unfulfilledDemandRepository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_whenUnfulfilledDemandsExist_shouldReturnAllUnfulfilledDemands() {
        storeRepository.saveAll(List.of(
            StoreBuilder.store1WithoutProductItemIds(),
            StoreBuilder.store2WithoutProductItemIds()
        ));
        List<UnfulfilledDemand> unfulfilledDemands = List.of(
            UnfulfilledDemandBuilder.unfulfilledDemand1WithoutId(),
            UnfulfilledDemandBuilder.unfulfilledDemand2WithoutId()
        );
        unfulfilledDemandRepository.saveAll(unfulfilledDemands);

        List<UnfulfilledDemand> found = unfulfilledDemandRepository.findAll();

        assertThat(found).usingRecursiveComparison().isEqualTo(unfulfilledDemands);
    }

    @Test
    void findAll_whenNoUnfulfilledDemands_shouldReturnEmptyList() {
        List<UnfulfilledDemand> found = unfulfilledDemandRepository.findAll();

        assertThat(found).isEmpty();
    }

    @Test
    void saveAll_shouldPersistMultipleUnfulfilledDemands() {
        storeRepository.saveAll(List.of(
            StoreBuilder.store1WithoutProductItemIds(),
            StoreBuilder.store2WithoutProductItemIds()
        ));

        List<UnfulfilledDemand> unfulfilledDemands = List.of(
            UnfulfilledDemandBuilder.unfulfilledDemand2WithoutId(),
            UnfulfilledDemandBuilder.unfulfilledDemand1WithoutId()
        );

        unfulfilledDemandRepository.saveAll(unfulfilledDemands);

        List<UnfulfilledDemand> found = unfulfilledDemandRepository.findAll();
        assertThat(found).usingRecursiveComparison().isEqualTo(unfulfilledDemands);
    }

    @Test
    void truncateAll_shouldRemoveAllUnfulfilledDemands() {
        storeRepository.saveAll(List.of(
            StoreBuilder.store1WithoutProductItemIds(),
            StoreBuilder.store2WithoutProductItemIds()
        ));
        List<UnfulfilledDemand> unfulfilledDemands = List.of(
            UnfulfilledDemandBuilder.unfulfilledDemand1WithoutId(),
            UnfulfilledDemandBuilder.unfulfilledDemand2WithoutId()
        );
        unfulfilledDemandRepository.saveAll(unfulfilledDemands);

        unfulfilledDemandRepository.truncateAll();

        List<UnfulfilledDemand> found = unfulfilledDemandRepository.findAll();
        assertThat(found).isEmpty();
    }
}