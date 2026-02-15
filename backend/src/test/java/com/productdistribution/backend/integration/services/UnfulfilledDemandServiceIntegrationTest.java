package com.productdistribution.backend.integration.services;

import com.productdistribution.backend.entities.UnfulfilledDemand;
import com.productdistribution.backend.repositories.StoreRepository;
import com.productdistribution.backend.repositories.UnfulfilledDemandRepository;
import com.productdistribution.backend.services.UnfulfilledDemandService;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.UnfulfilledDemandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UnfulfilledDemandServiceIntegrationTest {

    @Autowired
    private UnfulfilledDemandService unfulfilledDemandService;

    @Autowired
    private UnfulfilledDemandRepository unfulfilledDemandRepository;

    @Autowired
    private StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        storeRepository.saveAll(List.of(
            StoreBuilder.store1WithoutProductItemIds(),
            StoreBuilder.store2WithoutProductItemIds()
        ));
    }

    @Test
    void add_shouldSaveUnfulfilledDemand() {
        UnfulfilledDemand unfulfilledDemand = UnfulfilledDemandBuilder.unfulfilledDemand1WithoutId();

        unfulfilledDemandService.add(unfulfilledDemand);

        Optional<UnfulfilledDemand> found = unfulfilledDemandRepository.findById(unfulfilledDemand.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(unfulfilledDemand);
    }

    @Test
    void addAll_shouldSaveAllUnfulfilledDemands() {
        List<UnfulfilledDemand> unfulfilledDemands = List.of(
            UnfulfilledDemandBuilder.unfulfilledDemand1WithoutId(),
            UnfulfilledDemandBuilder.unfulfilledDemand2WithoutId()
        );

        unfulfilledDemandService.addAll(unfulfilledDemands);

        List<UnfulfilledDemand> found = unfulfilledDemandRepository.findAll();
        assertThat(found).usingRecursiveComparison().isEqualTo(unfulfilledDemands);
    }

    @Test
    void getAll_shouldReturnAllUnfulfilledDemands() {
        List<UnfulfilledDemand> unfulfilledDemands = List.of(
            UnfulfilledDemandBuilder.unfulfilledDemand1WithoutId(),
            UnfulfilledDemandBuilder.unfulfilledDemand2WithoutId()
        );
        unfulfilledDemandRepository.saveAll(unfulfilledDemands);

        List<UnfulfilledDemand> result = unfulfilledDemandService.getAll();

        assertThat(result).usingRecursiveComparison().isEqualTo(unfulfilledDemands);
    }

    @Test
    void getAll_whenNoUnfulfilledDemands_shouldReturnEmptyList() {
        List<UnfulfilledDemand> result = unfulfilledDemandService.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void deleteAll_shouldDeleteAllUnfulfilledDemands() {
        List<UnfulfilledDemand> unfulfilledDemands = List.of(
            UnfulfilledDemandBuilder.unfulfilledDemand1WithoutId(),
            UnfulfilledDemandBuilder.unfulfilledDemand2WithoutId()
        );
        unfulfilledDemandRepository.saveAll(unfulfilledDemands);

        unfulfilledDemandService.deleteAll();

        List<UnfulfilledDemand> found = unfulfilledDemandRepository.findAll();
        assertThat(found).isEmpty();
    }
}