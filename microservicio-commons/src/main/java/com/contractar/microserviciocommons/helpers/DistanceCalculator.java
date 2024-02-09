package com.contractar.microserviciocommons.helpers;

import org.locationtech.jts.geom.Point;

public class DistanceCalculator {
    // Radio de la Tierra en metros
    static final double RADIUS = 6371000;

    // Método para calcular la distancia entre dos puntos utilizando la fórmula de Haversine
    public static double calculateDistance(Point point1, Point point2) {
        double lat1 = Math.toRadians(point1.getY());
        double lon1 = Math.toRadians(point1.getX());
        double lat2 = Math.toRadians(point2.getY());
        double lon2 = Math.toRadians(point2.getX());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Fórmula de Haversine
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
     // Distancia en kilometros
        return RADIUS * c/1000;
    }
}