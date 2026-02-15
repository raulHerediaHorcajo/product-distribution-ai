package com.productdistribution.backend.unit.services;

import com.productdistribution.backend.entities.UnfulfilledDemand;
import com.productdistribution.backend.repositories.UnfulfilledDemandRepository;
import com.productdistribution.backend.services.UnfulfilledDemandService;
import com.productdistribution.backend.utils.UnfulfilledDemandBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnfulfilledDemandServiceUnitTest {

    @Mock
    private UnfulfilledDemandRepository unfulfilledDemandRepository;

    @InjectMocks
    private UnfulfilledDemandService unfulfilledDemandService;

    @Test
    void add_shouldSaveUnfulfilledDemand() {
        UnfulfilledDemand unfulfilledDemand = UnfulfilledDemandBuilder.unfulfilledDemand1();

        unfulfilledDemandService.add(unfulfilledDemand);

        verify(unfulfilledDemandRepository).save(unfulfilledDemand);
    }

    @Test
    void addAll_shouldSaveAllUnfulfilledDemands() {
        List<UnfulfilledDemand> unfulfilledDemands = List.of(
                UnfulfilledDemandBuilder.unfulfilledDemand1(),
                UnfulfilledDemandBuilder.unfulfilledDemand2()
        );

        unfulfilledDemandService.addAll(unfulfilledDemands);

        verify(unfulfilledDemandRepository).saveAll(unfulfilledDemands);
    }

    @Test
    void getAll_shouldReturnAllUnfulfilledDemands() {
        List<UnfulfilledDemand> expectedDemands = List.of(
                UnfulfilledDemandBuilder.unfulfilledDemand1(),
                UnfulfilledDemandBuilder.unfulfilledDemand2()
        );
        when(unfulfilledDemandRepository.findAll()).thenReturn(expectedDemands);

        List<UnfulfilledDemand> result = unfulfilledDemandService.getAll();

        verify(unfulfilledDemandRepository).findAll();
        assertThat(result).isEqualTo(expectedDemands);
    }

    @Test
    void deleteAll_shouldDeleteAllUnfulfilledDemands() {
        unfulfilledDemandService.deleteAll();

        verify(unfulfilledDemandRepository).truncateAll();
    }
}