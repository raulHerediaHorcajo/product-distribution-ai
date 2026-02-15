package com.productdistribution.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.productdistribution.backend.dtos.metrics.DetailedMetricsDTO;
import com.productdistribution.backend.dtos.metrics.GlobalMetricsDTO;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.services.MetricsService;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    @Autowired
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/global")
    public GlobalMetricsDTO getGlobalMetrics() {
        return metricsService.calculateGlobalMetrics();
    }

    @GetMapping("/detailed")
    public DetailedMetricsDTO getDetailedMetrics(@Valid StockAssignmentCriteria criteria) {
        return metricsService.calculateDetailedMetrics(criteria);
    }
}