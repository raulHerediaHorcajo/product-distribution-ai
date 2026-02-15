package com.productdistribution.backend.unit.schedulers;

import com.productdistribution.backend.schedulers.DistributionScheduler;
import com.productdistribution.backend.services.DistributionService;
import com.productdistribution.backend.utils.JsonChangeWatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistributionSchedulerUnitTest {

    @Mock
    private DistributionService distributionService;

    @Mock
    private JsonChangeWatcher jsonChangeWatcher;

    @InjectMocks
    private DistributionScheduler distributionScheduler;

    @Test
    void onStartup_shouldCallDistributeProductsAndUpdateContentHashes() {
        distributionScheduler.onStartup();

        verify(distributionService).distributeProducts();
        verify(jsonChangeWatcher).updateContentHashes();
    }

    @Test
    void nightlyDistribution_whenChangesDetected_shouldCallDistributeProducts() {
        when(jsonChangeWatcher.hasAnyChanged()).thenReturn(true);

        distributionScheduler.nightlyDistribution();

        verify(jsonChangeWatcher).hasAnyChanged();
        verify(distributionService).distributeProducts();
    }

    @Test
    void nightlyDistribution_whenNoChangesDetected_shouldNotCallDistributeProducts() {
        when(jsonChangeWatcher.hasAnyChanged()).thenReturn(false);

        distributionScheduler.nightlyDistribution();

        verify(jsonChangeWatcher).hasAnyChanged();
        verify(distributionService, never()).distributeProducts();
    }
}