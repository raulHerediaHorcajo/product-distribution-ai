package com.productdistribution.backend.schedulers;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.productdistribution.backend.services.DistributionService;
import com.productdistribution.backend.utils.JsonChangeWatcher;

@Component
@Profile("!test | e2e")
public class DistributionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DistributionScheduler.class);

    private final DistributionService distributionService;
    private final JsonChangeWatcher jsonChangeWatcher;

    @Autowired
    public DistributionScheduler(DistributionService distributionService, JsonChangeWatcher jsonChangeWatcher) {
        this.distributionService = distributionService;
        this.jsonChangeWatcher = jsonChangeWatcher;
    }

    @PostConstruct
    public void onStartup() {
        logger.info("🔁 Executing initial distribution on application startup...");
        distributionService.distributeProducts();
        jsonChangeWatcher.updateContentHashes();
    }

    @Scheduled(cron = "${scheduler.distribution.cron}")
    public void nightlyDistribution() {
        logger.info("🔍 Checking if JSON files have changed (comparing content hashes)...");

        if (jsonChangeWatcher.hasAnyChanged()) {
            logger.info("🔁 Changes detected in JSON files. Executing redistribution...");
            distributionService.distributeProducts();
            logger.info("✅ Distribution completed");
        } else {
            logger.info("⏭️  No changes detected in files. Distribution skipped.");
        }
    }
}