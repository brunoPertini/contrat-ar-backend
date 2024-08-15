package com.contractar.microserviciocommons.helpers;

import org.locationtech.jts.geom.Point;

public class DistanceCalculator {
    // Earth's radius
    static final double RADIUS = 6371;
    
    public static double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public static double calculateDistance(double startLat, double startLong, double endLat, double endLong) {

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIUS * c;
    }
    
    public static boolean isPointInsideRadius(Point center, double radius, Point checkingPoint) {
    	double distance =  calculateDistance(center.getX(), center.getY(), checkingPoint.getX(), checkingPoint.getY());
    	return distance <= radius;
    }
}