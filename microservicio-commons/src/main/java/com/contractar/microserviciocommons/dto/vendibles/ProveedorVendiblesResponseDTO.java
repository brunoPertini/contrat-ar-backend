package com.contractar.microserviciocommons.dto.vendibles;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;

/**
 * 
 * Response given to a Proveedor when is in its index page. All vendibles belong to the same Proveedor,
 * and the categories match with the vendibles given.
 *
 */
public class ProveedorVendiblesResponseDTO implements CategorizableVendiblesResponse{
	
	private Set<SimplifiedVendibleDTO> vendibles;
	
	private MultiValueMap<String, CategoryHierarchy> categorias;

	public ProveedorVendiblesResponseDTO() {
		this.categorias = new LinkedMultiValueMap<>();
		this.vendibles = new LinkedHashSet<>();
	}

	@Override
	public MultiValueMap<String, CategoryHierarchy> getCategorias() {
		return this.categorias;
	}

	@Override
	public void setCategorias(MultiValueMap<String, CategoryHierarchy> categorias) {
		this.categorias = categorias;
	}
	
	public Set<SimplifiedVendibleDTO> getVendibles() {
		return vendibles;
	}

	public void setVendibles(Set<SimplifiedVendibleDTO> vendibles) {
		this.vendibles = vendibles;
	}

}
