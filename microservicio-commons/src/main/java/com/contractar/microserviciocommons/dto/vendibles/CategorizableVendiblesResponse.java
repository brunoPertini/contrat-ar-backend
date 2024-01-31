package com.contractar.microserviciocommons.dto.vendibles;

import org.springframework.util.MultiValueMap;

import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;

public interface CategorizableVendiblesResponse {
	public MultiValueMap<String, CategoryHierarchy> getCategorias();

	public void setCategorias(MultiValueMap<String, CategoryHierarchy> categorias);
}
