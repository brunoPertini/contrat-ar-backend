package com.contractar.microserviciocommons.helpers;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.entities.ProveedorVendibleAccesor;

public class DistanceCalculator {
    // Earth's radius
    static final double RADIUS = 6371;
    
    private DistanceCalculator() {}
    
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
    
    public static double getToVendibleDistance(Point clienteLocation, ProveedorVendibleAccesor proveedorVendible) {
    	Point vendibleLocation = proveedorVendible.getLocation();
    	return calculateDistance(clienteLocation.getX(), clienteLocation.getY(), vendibleLocation.getX(), vendibleLocation.getY());
    }
    
    public static double getToProveedorDistance(Point clienteLocation, ProveedorVendibleAccesor proveedorVendible) {
    	Point proveedorLocation = proveedorVendible.getProveedor().getLocation();
    	return calculateDistance(clienteLocation.getX(), clienteLocation.getY(), proveedorLocation.getX(), proveedorLocation.getY());
    }
    
    /**
     * Sets the offer distance as the minimum among client's location, proveedor's location and offer's location
     * @param clienteLocation
     * @param proveedorVendible
     * @return The minimum distance from client's location to Proveedor or Vendible's location.
     */
    public static double resolveDistanceFromClient(Point clienteLocation, ProveedorVendibleAccesor proveedorVendible) {
    	double toProveedorDistance =  getToProveedorDistance(clienteLocation, proveedorVendible);
    	double toVendibleDistance = getToVendibleDistance(clienteLocation, proveedorVendible);
    	
    	if (proveedorVendible.getOffersDelivery() && proveedorVendible.getOffersInCustomAddress()) {
    		double minimumDistance = Math.min(toProveedorDistance, toVendibleDistance);
    		
    		if (minimumDistance == toProveedorDistance) {
    			proveedorVendible.setLocation(proveedorVendible.getProveedor().getLocation());
    		} else {
    			proveedorVendible.setLocation(proveedorVendible.getLocation());
    		}
    		
    		return Math.min(toProveedorDistance, toVendibleDistance);
    	} else if (proveedorVendible.getOffersDelivery()) {
    		proveedorVendible.setLocation(proveedorVendible.getProveedor().getLocation());
    		return toProveedorDistance;
    	}
    	
    	proveedorVendible.setLocation(proveedorVendible.getLocation());
    	return toVendibleDistance;
    }
}