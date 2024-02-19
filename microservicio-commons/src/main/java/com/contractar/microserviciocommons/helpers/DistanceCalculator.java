package com.contractar.microserviciocommons.helpers;

import java.text.DecimalFormat;

import org.locationtech.jts.geom.Point;

public class DistanceCalculator {
    // Earth's radius
    static final double RADIUS = 6371000;

    /**
     * 
     * @param point1
     * @param point2
     * @return The distance between point1 and point2 in kilometers, using Haversine's formula
     */
    public static double calculateDistance(Point point1, Point point2) {
        double lat1 = Math.toRadians(point1.getY());
        double lon1 = Math.toRadians(point1.getX());
        double lat2 = Math.toRadians(point2.getY());
        double lon2 = Math.toRadians(point2.getX());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        DecimalFormat format = new DecimalFormat("#.00");
        String numberString = format.format((RADIUS * c)/1000);
        
        return Double.valueOf(numberString.replace(',', '.'));
    }
}