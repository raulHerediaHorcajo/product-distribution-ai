package com.productdistribution.backend.unit.services;

import com.productdistribution.backend.services.GeoDistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class GeoDistanceServiceUnitTest {

    private GeoDistanceService geoDistanceService;

    @BeforeEach
    void setUp() {
        geoDistanceService = new GeoDistanceService();
    }

    @Test
    void calculateHaversineDistance_whenSameCoordinates_shouldReturnZero() {
        double lat = 40.4168;
        double lon = -3.7038;

        double distance = geoDistanceService.calculateHaversineDistance(lat, lon, lat, lon);

        assertThat(distance).isZero();
    }

    @Test
    void calculateHaversineDistance_whenMadridToBarcelona_shouldReturnCorrectDistance() {
        double madridLat = 40.4168;
        double madridLon = -3.7038;
        double barcelonaLat = 41.3851;
        double barcelonaLon = 2.1734;

        double expectedDistance = 504.0;
        double tolerance = 5.0;

        double distance = geoDistanceService.calculateHaversineDistance(
            madridLat, madridLon, barcelonaLat, barcelonaLon);

        assertThat(distance).isCloseTo(expectedDistance, within(tolerance));
    }

    @Test
    void calculateHaversineDistance_whenSameLatitude_shouldReturnCorrectDistance() {
        double lat = 40.4168;
        double lon1 = -3.7038;
        double lon2 = 6.2962;

        double expectedDistance = 850.0;
        double tolerance = 5.0;

        double distance = geoDistanceService.calculateHaversineDistance(lat, lon1, lat, lon2);

        assertThat(distance).isCloseTo(expectedDistance, within(tolerance));
    }

    @Test
    void calculateHaversineDistance_whenSameLongitude_shouldReturnCorrectDistance() {
        double lat1 = 40.4168;
        double lat2 = 50.4168;
        double lon = -3.7038;

        double expectedDistance = 1110.0;
        double tolerance = 5.0;

        double distance = geoDistanceService.calculateHaversineDistance(lat1, lon, lat2, lon);

        assertThat(distance).isCloseTo(expectedDistance, within(tolerance));
    }

    @Test
    void calculateHaversineDistance_whenNegativeCoordinates_shouldReturnCorrectDistance() {
        double lat1 = -34.6037;
        double lon1 = -58.3816;
        double lat2 = -33.4489;
        double lon2 = -70.6693;

        double expectedDistance = 1137.0;
        double tolerance = 5.0;

        double distance = geoDistanceService.calculateHaversineDistance(
            lat1, lon1, lat2, lon2);

        assertThat(distance).isCloseTo(expectedDistance, within(tolerance));
    }

    @Test
    void calculateHaversineDistance_whenEquatorCoordinates_shouldReturnCorrectDistance() {
        double lat1 = 0.0;
        double lon1 = 0.0;
        double lat2 = 0.0;
        double lon2 = 1.0;

        double expectedDistance = 111.0;
        double tolerance = 1.0;

        double distance = geoDistanceService.calculateHaversineDistance(lat1, lon1, lat2, lon2);

        assertThat(distance).isCloseTo(expectedDistance, within(tolerance));
    }

    @Test
    void calculateHaversineDistance_whenDateLineCrossing_shouldReturnCorrectDistance() {
        double lat1 = 0.0;
        double lon1 = 179.0;
        double lat2 = 0.0;
        double lon2 = -179.0;

        double expectedDistance = 222.0;
        double tolerance = 1.0;

        double distance = geoDistanceService.calculateHaversineDistance(lat1, lon1, lat2, lon2);

        assertThat(distance).isCloseTo(expectedDistance, within(tolerance));
    }

    @Test
    void calculateHaversineDistance_whenVerySmallDistances_shouldReturnCorrectDistance() {
        double lat1 = 40.4168;
        double lon1 = -3.7038;
        double lat2 = 40.4168001;
        double lon2 = -3.7038001;

        double expectedDistance = 0.01;
        double tolerance = 0.01;

        double distance = geoDistanceService.calculateHaversineDistance(lat1, lon1, lat2, lon2);

        assertThat(distance).isCloseTo(expectedDistance, within(tolerance));
    }

    @Test
    void calculateHaversineDistance_whenDifferentHemispheres_shouldReturnCorrectDistance() {
        double lat1 = 40.4168;
        double lon1 = -3.7038;
        double lat2 = -34.6037;
        double lon2 = -58.3816;

        double expectedDistance = 10000.0;
        double tolerance = 50.0;

        double distance = geoDistanceService.calculateHaversineDistance(lat1, lon1, lat2, lon2);

        assertThat(distance).isCloseTo(expectedDistance, within(tolerance));
    }
}