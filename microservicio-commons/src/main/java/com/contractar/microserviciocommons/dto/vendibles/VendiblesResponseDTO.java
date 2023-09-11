package com.contractar.microserviciocommons.dto.vendibles;

import java.util.LinkedHashSet;
import java.util.Set;

import com.contractar.microserviciocommons.dto.ProveedorDTO;

public class VendiblesResponseDTO {
	private Set<SimplifiedProveedorVendibleDTO> vendibles;
	private Set<ProveedorDTO> proveedores;
	
	public VendiblesResponseDTO() {
		this.vendibles = new LinkedHashSet<SimplifiedProveedorVendibleDTO>();
		this.proveedores = new LinkedHashSet<ProveedorDTO>();
	}

	public VendiblesResponseDTO(Set<SimplifiedProveedorVendibleDTO> vendibles, Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
	}
	
	public Set<SimplifiedProveedorVendibleDTO> getVendibles() {
		return vendibles;
	}
	public void setVendibles(Set<SimplifiedProveedorVendibleDTO> vendibles) {
		this.vendibles = vendibles;
	}
	public Set<ProveedorDTO> getProveedores() {
		return proveedores;
	}
	public void setProveedores(Set<ProveedorDTO> proveedores) {
		this.proveedores = proveedores;
	}
}
