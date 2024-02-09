package com.contractar.microserviciousuario.dtos;

import com.contractar.microserviciocommons.dto.proveedorvendible.SimplifiedProveedorVendibleDTO;

public class DistanceProveedorDTO extends SimplifiedProveedorVendibleDTO{

	private double distance;

	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public DistanceProveedorDTO() {
		super();
	}
	public DistanceProveedorDTO(String vendibleNombre, String descripcion, int precio, String imagenUrl,
			int stock, Long proveedorId) {
		super(vendibleNombre, descripcion, precio, imagenUrl, stock, proveedorId);
	}
	public DistanceProveedorDTO(String vendibleNombre, String descripcion, int precio, String imagenUrl,
			int stock, Long proveedorId, double distance) {
		super(vendibleNombre, descripcion, precio, imagenUrl, stock, proveedorId);
		this.distance = distance;
	}

}
