package com.productdistribution.backend.unit.entities;

import com.productdistribution.backend.entities.ProductItem;
import com.productdistribution.backend.utils.ProductItemBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductItemUnitTest {

    @Test
    void reduceQuantity_shouldReduceQuantity() {
        ProductItem productItem = ProductItemBuilder.productItem1();

        productItem.reduceQuantity(20);

        assertThat(productItem.getQuantity()).isEqualTo(30);
    }

    @Test
    void reduceQuantity_whenAmountEqualsQuantity_shouldReduceToZero() {
        ProductItem productItem = ProductItemBuilder.productItem1();

        productItem.reduceQuantity(50);

        assertThat(productItem.getQuantity()).isZero();
    }

    @Test
    void reduceQuantity_whenAmountGreaterThanQuantity_shouldThrowIllegalArgumentException() {
        ProductItem productItem = ProductItemBuilder.productItem1();

        assertThatThrownBy(() -> productItem.reduceQuantity(60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot reduce by more than available")
                .hasMessageContaining("Available: 50")
                .hasMessageContaining("requested: 60");
    }

    @Test
    void hasStock_shouldReturnTrue() {
        ProductItem productItem = ProductItemBuilder.productItem1();

        assertThat(productItem.hasStock()).isTrue();
    }

    @Test
    void hasStock_whenQuantityIsZero_shouldReturnFalse() {
        ProductItem productItem = ProductItemBuilder.builder()
                .withQuantity(0)
                .build();

        assertThat(productItem.hasStock()).isFalse();
    }

    @Test
    void hasStock_whenQuantityIsNegative_shouldReturnFalse() {
        ProductItem productItem = ProductItemBuilder.builder()
                .withQuantity(-5)
                .build();

        assertThat(productItem.hasStock()).isFalse();
    }
}