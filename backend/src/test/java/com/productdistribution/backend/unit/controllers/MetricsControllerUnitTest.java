package com.productdistribution.backend.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdistribution.backend.controllers.MetricsController;
import com.productdistribution.backend.dtos.metrics.DetailedMetricsDTO;
import com.productdistribution.backend.dtos.metrics.GlobalMetricsDTO;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.services.MetricsService;
import com.productdistribution.backend.utils.metrics.DetailedMetricsDTOBuilder;
import com.productdistribution.backend.utils.metrics.GlobalMetricsDTOBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetricsController.class)
@ActiveProfiles("test")
class MetricsControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MetricsService metricsService;

    @Test
    void getGlobalMetrics_shouldReturn200WithGlobalMetrics() throws Exception {
        GlobalMetricsDTO globalMetrics = GlobalMetricsDTOBuilder.globalMetrics1();
        String expectedJson = objectMapper.writeValueAsString(globalMetrics);

        when(metricsService.calculateGlobalMetrics()).thenReturn(globalMetrics);

        mockMvc.perform(get("/api/metrics/global"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(metricsService).calculateGlobalMetrics();
    }

    @Test
    void getDetailedMetrics_shouldReturn200WithDetailedMetrics() throws Exception {
        DetailedMetricsDTO detailedMetrics = DetailedMetricsDTOBuilder.detailedMetrics1();
        String expectedJson = objectMapper.writeValueAsString(detailedMetrics);

        when(metricsService.calculateDetailedMetrics(any(StockAssignmentCriteria.class))).thenReturn(detailedMetrics);

        mockMvc.perform(get("/api/metrics/detailed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(metricsService).calculateDetailedMetrics(any(StockAssignmentCriteria.class));
    }
}