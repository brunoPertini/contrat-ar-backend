package com.contractar.microserviciocommons.helpers;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.entities.ProveedorVendibleAccesor;

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
    
    /**
     * 
     * @param clienteLocation
     * @param proveedorVendible
     * @return The minimum distance from cliente location to Proveedor or Vendible's location.
     */
    public static double resolveDistanceFromClient(Point clienteLocation, ProveedorVendibleAccesor proveedorVendible) {
    	Point proveedorLocation = proveedorVendible.getProveedor().getLocation();
    	Point vendibleLocation = proveedorVendible.getLocation();
    	
    	double toProveedorDistance =  calculateDistance(clienteLocation.getX(), clienteLocation.getY(), proveedorLocation.getX(), proveedorLocation.getY());
    	double toVendibleDistance = calculateDistance(clienteLocation.getX(), clienteLocation.getY(), vendibleLocation.getX(), vendibleLocation.getY());
    	
    	if (proveedorVendible.getOffersDelivery() && proveedorVendible.getOffersInCustomAddress()) {
    		return Math.min(toProveedorDistance, toVendibleDistance);
    	} else if (proveedorVendible.getOffersDelivery()) {
    		return toProveedorDistance;
    	}
    	
    	return toVendibleDistance;
    }
}