package com.contractar.microserviciocommons.dto.vendibles;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.proveedorvendible.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;

public class VendiblesResponseDTO implements CategorizableVendiblesResponse{
	private Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles;
	private Set<ProveedorDTO> proveedores;
	private Map<String, CategoryHierarchy> categorias;
	
	public VendiblesResponseDTO() {
		this.vendibles = new LinkedHashMap<String, Set<SimplifiedProveedorVendibleDTO>>();
		this.proveedores = new LinkedHashSet<ProveedorDTO>();
		this.categorias = new HashMap<String, CategoryHierarchy>();
	}

	public VendiblesResponseDTO(Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles,
			Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
		this.categorias = new HashMap<String, CategoryHierarchy>();
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

	@Override
	public Map<String, CategoryHierarchy> getCategorias() {
		return categorias;
	}

	@Override
	public void setCategorias(Map<String, CategoryHierarchy> categorias) {
		this.categorias = categorias;
	}
}
