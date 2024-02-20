package com.contractar.microserviciousuario.dtos;

import java.util.Objects;

import com.contractar.microserviciocommons.dto.proveedorvendible.SimplifiedProveedorVendibleDTO;

public class DistanceProveedorDTO extends SimplifiedProveedorVendibleDTO {

	private double distance;

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public DistanceProveedorDTO() {
	}

	public DistanceProveedorDTO(String vendibleNombre, String descripcion, int precio, String imagenUrl, int stock,
			Long proveedorId) {
		super(vendibleNombre, descripcion, precio, imagenUrl, stock, proveedorId);
	}

	public DistanceProveedorDTO(String vendibleNombre, String descripcion, int precio, String imagenUrl, int stock,
			Long proveedorId, double distance) {
		super(vendibleNombre, descripcion, precio, imagenUrl, stock, proveedorId);
		this.distance = distance;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		DistanceProveedorDTO dto = (DistanceProveedorDTO) obj;

		if (this.getProveedorId() != null) {
			return this.getProveedorId() == dto.getProveedorId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getProveedorId());
	}

}
