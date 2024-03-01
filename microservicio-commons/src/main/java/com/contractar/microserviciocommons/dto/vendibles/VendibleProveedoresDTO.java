package com.contractar.microserviciocommons.dto.vendibles;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciousuario.dtos.DistanceProveedorDTO;

/**
 * 
 * Response given to a cliente when enters into a vendible detail. Contains
 * proveedores offers, each one with its distance from cliente current's
 * location. It also has some additional information useful for frontend, such as
 * min and max distances found.
 *
 */
public class VendibleProveedoresDTO {
	private Set<DistanceProveedorDTO> vendibles;
	private Set<ProveedorDTO> proveedores;

	private double minDistance;
	private double maxDistance;
	private int minPrice;
	private int maxPrice;



	public VendibleProveedoresDTO(Set<DistanceProveedorDTO> vendibles, Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
	}

	public VendibleProveedoresDTO() {
		this.vendibles = new HashSet<>();
		this.proveedores = new HashSet<>();
	}

	public VendibleProveedoresDTO(Comparator<DistanceProveedorDTO> comparator) {
		this.vendibles = new TreeSet<>(comparator);
		this.proveedores = new HashSet<>();
	}

	public Set<DistanceProveedorDTO> getVendibles() {
		return vendibles;
	}

	public void setVendibles(Set<DistanceProveedorDTO> vendibles) {
		this.vendibles = vendibles;
	}

	public Set<ProveedorDTO> getProveedores() {
		return proveedores;
	}

	public void setProveedores(Set<ProveedorDTO> proveedores) {
		this.proveedores = proveedores;
	}

	public double getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}
	public int getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}
}
