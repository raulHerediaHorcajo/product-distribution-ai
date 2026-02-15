package com.productdistribution.backend.unit.entities;

import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.utils.StoreBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StoreUnitTest {

    @Test
    void calculateAdjustedDemand_shouldCalculateAdjustedDemand() {
        Store store = StoreBuilder.defaultStore();

        int adjustedDemand = store.calculateAdjustedDemand(100);

        assertThat(adjustedDemand).isEqualTo(90);
    }

    @Test
    void calculateAdjustedDemand_whenReturnRateIsZero_shouldReturnOriginalDemand() {
        Store store = StoreBuilder.builder()
                .withExpectedReturnRate(0.0)
                .build();

        int adjustedDemand = store.calculateAdjustedDemand(100);

        assertThat(adjustedDemand).isEqualTo(100);
    }

    @Test
    void calculateAdjustedDemand_whenReturnRateIsOne_shouldReturnZero() {
        Store store = StoreBuilder.builder()
                .withExpectedReturnRate(1.0)
                .build();

        int adjustedDemand = store.calculateAdjustedDemand(100);

        assertThat(adjustedDemand).isZero();
    }

    @Test
    void hasCapacityFor_shouldReturnTrue() {
        Store store = StoreBuilder.defaultStore();

        assertThat(store.hasCapacityFor(50)).isTrue();
    }

    @Test
    void hasCapacityFor_whenCapacityIsExact_shouldReturnTrue() {
        Store store = StoreBuilder.builder()
                .withRemainingCapacity(50)
                .build();

        assertThat(store.hasCapacityFor(50)).isTrue();
    }

    @Test
    void hasCapacityFor_whenCapacityIsInsufficient_shouldReturnFalse() {
        Store store = StoreBuilder.builder()
                .withRemainingCapacity(30)
                .build();

        assertThat(store.hasCapacityFor(50)).isFalse();
    }

    @Test
    void tryAllocateCapacity_shouldAllocateAndReturnTrue() {
        Store store = StoreBuilder.defaultStore();

        boolean result = store.tryAllocateCapacity(50);

        assertThat(result).isTrue();
        assertThat(store.getRemainingCapacity()).isEqualTo(50);
    }

    @Test
    void tryAllocateCapacity_whenCapacityIsExact_shouldAllocateAndReturnTrue() {
        Store store = StoreBuilder.builder()
                .withRemainingCapacity(50)
                .build();

        boolean result = store.tryAllocateCapacity(50);

        assertThat(result).isTrue();
        assertThat(store.getRemainingCapacity()).isZero();
    }

    @Test
    void tryAllocateCapacity_whenCapacityIsInsufficient_shouldNotAllocateAndReturnFalse() {
        Store store = StoreBuilder.builder()
                .withRemainingCapacity(30)
                .build();

        boolean result = store.tryAllocateCapacity(50);

        assertThat(result).isFalse();
        assertThat(store.getRemainingCapacity()).isEqualTo(30);
    }

    @Test
    void setMaxStockCapacity_shouldSetMaxStockCapacityAndRemainingCapacity() {
        Store store = StoreBuilder.builder()
                .withMaxStockCapacity(50)
                .withRemainingCapacity(30)
                .build();

        store.setMaxStockCapacity(100);

        assertThat(store.getMaxStockCapacity()).isEqualTo(100);
        assertThat(store.getRemainingCapacity()).isEqualTo(100);
    }
}