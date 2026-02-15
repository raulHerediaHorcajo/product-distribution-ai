package com.productdistribution.backend.services;

import org.springframework.stereotype.Service;

@Service
public class GeoDistanceService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = haversine(latDistance) + Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2)) * haversine(lonDistance);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    private double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}