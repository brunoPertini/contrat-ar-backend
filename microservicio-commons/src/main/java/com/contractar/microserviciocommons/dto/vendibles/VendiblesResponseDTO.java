package com.contractar.microserviciocommons.dto.vendibles;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.contractar.microserviciocommons.dto.proveedorvendible.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;

public class VendiblesResponseDTO implements CategorizableVendiblesResponse{
	private Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles;
	private Set<ProveedorDTO> proveedores;
	private MultiValueMap<String, CategoryHierarchy> categorias;
	
	public VendiblesResponseDTO() {
		this.vendibles = new LinkedHashMap<>();
		this.proveedores = new LinkedHashSet<>();
		this.categorias = new LinkedMultiValueMap<>();
	}

	public VendiblesResponseDTO(Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles,
			Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
		this.categorias = new LinkedMultiValueMap<>();
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
	public MultiValueMap<String, CategoryHierarchy> getCategorias() {
		return categorias;
	}

	@Override
	public void setCategorias(MultiValueMap<String, CategoryHierarchy> categorias) {
		this.categorias = categorias;
	}
}
