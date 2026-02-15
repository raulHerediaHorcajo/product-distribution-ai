package com.productdistribution.backend.unit.repositories.specification;

import com.productdistribution.backend.entities.StockAssignment;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.repositories.specification.StockAssignmentSpecifications;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockAssignmentSpecificationsUnitTest {

    @Mock
    private Root<StockAssignment> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<String> storeIdPath;

    @Mock
    private Path<String> warehouseIdPath;

    @Mock
    private Path<String> productIdPath;

    @Mock
    private Predicate storeIdPredicate;

    @Mock
    private Predicate warehouseIdPredicate;

    @Mock
    private Predicate productIdPredicate;

    @Mock
    private Predicate conjunction;

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void withCriteria_whenAllCriteriaProvided_shouldBuildAllPredicates() {
        StockAssignmentCriteria criteria = new StockAssignmentCriteria();
        criteria.setStoreId("S1");
        criteria.setWarehouseId("W1");
        criteria.setProductId("P1");

        when(cb.conjunction()).thenReturn(conjunction);
        when(root.get("storeId")).thenReturn((Path) storeIdPath);
        when(root.get("warehouseId")).thenReturn((Path) warehouseIdPath);
        when(root.get("productId")).thenReturn((Path) productIdPath);
        when(cb.equal(storeIdPath, "S1")).thenReturn(storeIdPredicate);
        when(cb.equal(warehouseIdPath, "W1")).thenReturn(warehouseIdPredicate);
        when(cb.equal(productIdPath, "P1")).thenReturn(productIdPredicate);
        when(cb.and(conjunction, storeIdPredicate)).thenReturn(conjunction);
        when(cb.and(conjunction, warehouseIdPredicate)).thenReturn(conjunction);
        when(cb.and(conjunction, productIdPredicate)).thenReturn(conjunction);

        Specification<StockAssignment> specification = StockAssignmentSpecifications.withCriteria(criteria);
        Predicate result = specification.toPredicate(root, query, cb);

        verify(cb).conjunction();
        verify(root).get("storeId");
        verify(cb).equal(storeIdPath, "S1");
        verify(cb).and(conjunction, storeIdPredicate);
        verify(root).get("warehouseId");
        verify(cb).equal(warehouseIdPath, "W1");
        verify(cb).and(conjunction, warehouseIdPredicate);
        verify(root).get("productId");
        verify(cb).equal(productIdPath, "P1");
        verify(cb).and(conjunction, productIdPredicate);
        assertThat(result).isNotNull();
    }

    @Test
    void withCriteria_whenNoCriteriaProvided_shouldReturnConjunctionOnly() {
        StockAssignmentCriteria criteria = new StockAssignmentCriteria();

        when(cb.conjunction()).thenReturn(conjunction);

        Specification<StockAssignment> specification = StockAssignmentSpecifications.withCriteria(criteria);
        Predicate result = specification.toPredicate(root, query, cb);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
        verify(cb, never()).equal(any(), any());
        verify(cb, never()).and(any(Predicate.class), any(Predicate.class));
        assertThat(result).isNotNull();
    }

    @Test
    void withCriteria_whenAllCriteriaAreEmpty_shouldReturnConjunctionOnly() {
        StockAssignmentCriteria criteria = new StockAssignmentCriteria();
        criteria.setStoreId("");
        criteria.setWarehouseId("");
        criteria.setProductId("");

        when(cb.conjunction()).thenReturn(conjunction);

        Specification<StockAssignment> specification = StockAssignmentSpecifications.withCriteria(criteria);
        Predicate result = specification.toPredicate(root, query, cb);

        verify(cb).conjunction();
        verify(root, never()).get(anyString());
        verify(cb, never()).equal(any(), any());
        verify(cb, never()).and(any(Predicate.class), any(Predicate.class));
        assertThat(result).isNotNull();
    }
}