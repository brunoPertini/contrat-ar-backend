package com.contractar.microserviciocommons.dto.vendibles;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;

/**
 * 
 * Response given to a Proveedor when is in its index page. All vendibles belong to the same Proveedor,
 * and the categories match with the vendibles given.
 *
 */
public class ProveedorVendiblesResponseDTO implements CategorizableVendiblesResponse{
	
	private Set<SimplifiedVendibleDTO> vendibles;
	
	private Map<String, CategoryHierarchy> categorias;

	public ProveedorVendiblesResponseDTO() {
		this.categorias = new HashMap<String, CategoryHierarchy>();
		this.vendibles = new LinkedHashSet<SimplifiedVendibleDTO>();
	}

	@Override
	public Map<String, CategoryHierarchy> getCategorias() {
		return this.categorias;
	}

	@Override
	public void setCategorias(Map<String, CategoryHierarchy> categorias) {
		this.categorias = categorias;
	}
	
	public Set<SimplifiedVendibleDTO> getVendibles() {
		return vendibles;
	}

	public void setVendibles(Set<SimplifiedVendibleDTO> vendibles) {
		this.vendibles = vendibles;
	}

}
