package com.contractar.microserviciocommons.dto.vendibles;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciousuario.dtos.DistanceProveedorDTO;

/**
 * 
 * Response given to a cliente when enters into a vendible detail. Contains proveedores offers, each one
 * with its distance from cliente current's location.
 *
 */
public class VendibleProveedoresDTO {

	private Set<DistanceProveedorDTO> vendibles;
	private Set<ProveedorDTO> proveedores;

	public VendibleProveedoresDTO(Set<DistanceProveedorDTO> vendibles, Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
	}

	public VendibleProveedoresDTO() {
		this.vendibles = new TreeSet<>();
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
}
