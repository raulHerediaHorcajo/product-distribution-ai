package com.productdistribution.backend.e2e.controllers;

import com.productdistribution.backend.dtos.metrics.DetailedMetricsDTO;
import com.productdistribution.backend.dtos.metrics.GlobalMetricsDTO;
import com.productdistribution.backend.utils.metrics.CapacityUtilizationDTOBuilder;
import com.productdistribution.backend.utils.metrics.DetailedMetricsDTOBuilder;
import com.productdistribution.backend.utils.metrics.DistanceStatsDTOBuilder;
import com.productdistribution.backend.utils.metrics.GlobalMetricsDTOBuilder;
import com.productdistribution.backend.utils.metrics.StoresServedDTOBuilder;
import com.productdistribution.backend.utils.metrics.TopProductDTOBuilder;
import com.productdistribution.backend.utils.metrics.UnfulfilledDemandDTOBuilder;
import com.productdistribution.backend.utils.metrics.WarehouseUnitsDTOBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "e2e"})
@AutoConfigureWebTestClient
@Transactional
class MetricsControllerE2ETest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getGlobalMetrics_shouldReturn200WithGlobalMetrics() {
        GlobalMetricsDTO expectedMetrics = GlobalMetricsDTOBuilder.builder()
                .withTotalShipments(3L)
                .withFulfilledUnits(100L)
                .withUnfulfilledUnits(62L)
                .withAverageDistance(680.0)
                .build();

        webTestClient.get()
                .uri("/api/metrics/global")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(GlobalMetricsDTO.class)
                .value(metrics -> {
                    assertThat(metrics).usingRecursiveComparison()
                            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1, "averageDistance")
                            .isEqualTo(expectedMetrics);
                });
    }

    @Test
    void getDetailedMetrics_whenNoFilters_shouldReturn200WithDetailedMetrics() {
        DetailedMetricsDTO expectedMetrics = DetailedMetricsDTOBuilder.builder()
                .withEfficiencyScore(66)
                .withFulfillmentRate(61.73)
                .withTotalDistance(2040.0)
                .withTotalShipments(3L)
                .withStoresServed(StoresServedDTOBuilder.builder()
                        .withServedStores(1)
                        .withFullyServedStores(0)
                        .withNeverServedStores(1)
                        .withCoveragePercentage(50.0)
                        .withFullyServedPercentage(0.0)
                        .build())
                .withAvgShipmentSize(33.33)
                .withUniqueProductsDistributed(2)
                .withUniqueProductsRequested(3)
                .withCapacityUtilization(CapacityUtilizationDTOBuilder.builder()
                        .withPercentage(50.0)
                        .withStoresAtCapacity(1)
                        .withTotalStores(2)
                        .build())
                .withUnfulfilledDemand(UnfulfilledDemandDTOBuilder.builder()
                        .withTotalUnits(62L)
                        .withUnitsByStockShortage(27L)
                        .withUnitsByCapacityShortage(35L)
                        .build())
                .withFulfilledUnits(100L)
                .withDistanceStats(DistanceStatsDTOBuilder.builder()
                        .withMinDistance(500.0)
                        .withMaxDistance(1000.0)
                        .withMedianDistance(540.0)
                        .build())
                .withUnitsByWarehouse(List.of(
                        WarehouseUnitsDTOBuilder.builder()
                                .withWarehouseId("W2")
                                .withTotalUnits(70L)
                                .withPercentage(70.0)
                                .withAvgDistance(540.0)
                                .build(),
                        WarehouseUnitsDTOBuilder.builder()
                                .withWarehouseId("W1")
                                .withTotalUnits(20L)
                                .withPercentage(20.0)
                                .withAvgDistance(500.0)
                                .build(),
                        WarehouseUnitsDTOBuilder.builder()
                                .withWarehouseId("W3")
                                .withTotalUnits(10L)
                                .withPercentage(10.0)
                                .withAvgDistance(1000.0)
                                .build()
                ))
                .withTopProducts(List.of(
                        TopProductDTOBuilder.builder()
                                .withProductId("P1")
                                .withTotalQuantity(90L)
                                .build(),
                        TopProductDTOBuilder.builder()
                                .withProductId("P2")
                                .withTotalQuantity(10L)
                                .build()
                ))
                .build();

        webTestClient.get()
                .uri("/api/metrics/detailed")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(DetailedMetricsDTO.class)
                .value(metrics -> {
                    assertThat(metrics).usingRecursiveComparison()
                            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1,
                            "fulfillmentRate", "avgShipmentSize", "distanceStats.minDistance", "distanceStats.maxDistance",
                            "distanceStats.medianDistance", "unitsByWarehouse.avgDistance", "totalDistance")
                            .isEqualTo(expectedMetrics);
                });
    }

    @Test
    void getDetailedMetrics_whenAllFilters_shouldReturnFilteredDetailedMetrics() {
        String storeId = "S1";
        String warehouseId = "W1";
        String productId = "P1";
        DetailedMetricsDTO expectedMetrics = DetailedMetricsDTOBuilder.builder()
                .withEfficiencyScore(98)
                .withFulfillmentRate(100.0)
                .withTotalDistance(500.0)
                .withTotalShipments(1L)
                .withStoresServed(StoresServedDTOBuilder.builder()
                        .withServedStores(1)
                        .withFullyServedStores(1)
                        .withNeverServedStores(0)
                        .withCoveragePercentage(100.0)
                        .withFullyServedPercentage(100.0)
                        .build())
                .withAvgShipmentSize(20.0)
                .withUniqueProductsDistributed(1)
                .withUniqueProductsRequested(1)
                .withCapacityUtilization(CapacityUtilizationDTOBuilder.builder()
                        .withPercentage(100.0)
                        .withStoresAtCapacity(1)
                        .withTotalStores(1)
                        .build())
                .withUnfulfilledDemand(UnfulfilledDemandDTOBuilder.builder()
                        .withTotalUnits(0L)
                        .withUnitsByStockShortage(0L)
                        .withUnitsByCapacityShortage(0L)
                        .build())
                .withFulfilledUnits(20L)
                .withDistanceStats(DistanceStatsDTOBuilder.builder()
                        .withMinDistance(500.0)
                        .withMaxDistance(500.0)
                        .withMedianDistance(500.0)
                        .build())
                .withUnitsByWarehouse(List.of(
                        WarehouseUnitsDTOBuilder.builder()
                            .withWarehouseId("W1")
                            .withTotalUnits(20L)
                            .withPercentage(100.0)
                            .withAvgDistance(500.0)
                            .build()
                ))
                .withTopProducts(List.of(
                        TopProductDTOBuilder.builder()
                                .withProductId("P1")
                                .withTotalQuantity(20L)
                                .build()
                ))
                .build();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/metrics/detailed")
                        .queryParam("storeId", storeId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("productId", productId)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(DetailedMetricsDTO.class)
                .value(metrics -> {
                    assertThat(metrics).usingRecursiveComparison()
                            .withEqualsForFields((Double a, Double b) -> Math.abs(a - b) < 0.1,
                            "distanceStats.minDistance", "distanceStats.medianDistance",
                            "distanceStats.maxDistance", "unitsByWarehouse.avgDistance", "totalDistance")
                            .isEqualTo(expectedMetrics);
                });
    }
}