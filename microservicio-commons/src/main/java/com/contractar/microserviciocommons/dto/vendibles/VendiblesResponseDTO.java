package com.contractar.microserviciocommons.dto.vendibles;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.contractar.microserviciocommons.dto.ProveedorDTO;

public class VendiblesResponseDTO {
	private Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles;
	private Set<ProveedorDTO> proveedores;
	private Set<VendibleCategoryDTO> categorias;

	public VendiblesResponseDTO() {
		this.vendibles = new LinkedHashMap<String, Set<SimplifiedProveedorVendibleDTO>>();
		this.proveedores = new LinkedHashSet<ProveedorDTO>();
		this.categorias = new LinkedHashSet<VendibleCategoryDTO>();
	}

	public VendiblesResponseDTO(Map<String, Set<SimplifiedProveedorVendibleDTO>>vendibles, Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
	}
	
	public Map<String, Set<SimplifiedProveedorVendibleDTO>> getVendibles() {
		return vendibles;
	}
	public void setVendibles(Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles) {
		this.vendibles = vendibles;
	}
	public Set<ProveedorDTO> getProveedores() {
		return proveedores;
	}
	public void setProveedores(Set<ProveedorDTO> proveedores) {
		this.proveedores = proveedores;
	}
	public Set<VendibleCategoryDTO> getCategorias() {
		return categorias;
	}

	public void setCategorias(Set<VendibleCategoryDTO> categorias) {
		this.categorias = categorias;
	}
}
