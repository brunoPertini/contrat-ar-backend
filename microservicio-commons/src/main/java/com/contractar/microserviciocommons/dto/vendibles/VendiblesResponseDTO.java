package com.contractar.microserviciocommons.dto.vendibles;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy.CategoryHierachyDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class VendiblesResponseDTO {
	private Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles;
	private Set<ProveedorDTO> proveedores;
	@JsonIgnore
	private Map<String, CategoryHierarchy> categorias;
	
	private Map<String, CategoryHierachyDTO> categoriasVendibles;

	public VendiblesResponseDTO() {
		this.vendibles = new LinkedHashMap<String, Set<SimplifiedProveedorVendibleDTO>>();
		this.proveedores = new LinkedHashSet<ProveedorDTO>();
		this.categorias = new HashMap<String, CategoryHierarchy>();
		this.categoriasVendibles = new HashMap<String, CategoryHierachyDTO>();
	}

	public VendiblesResponseDTO(Map<String, Set<SimplifiedProveedorVendibleDTO>> vendibles,
			Set<ProveedorDTO> proveedores) {
		this.vendibles = vendibles;
		this.proveedores = proveedores;
		this.categorias = new HashMap<String, CategoryHierarchy>();
		this.categoriasVendibles = new HashMap<String, CategoryHierachyDTO>();
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

	public Map<String, CategoryHierarchy> getCategorias() {
		return categorias;
	}

	public void setCategorias(Map<String, CategoryHierarchy> categorias) {
		this.categorias = categorias;
	}
	
	public Map<String, CategoryHierachyDTO> getCategoriasVendibles() {
		return categoriasVendibles;
	}

	public void setCategoriasVendibles(Map<String, CategoryHierachyDTO> categoriasVendibles) {
		this.categoriasVendibles = categoriasVendibles;
	}

}
