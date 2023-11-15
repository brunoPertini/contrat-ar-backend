package com.contractar.microserviciocommons.dto.vendibles;

import java.util.Map;

import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;

public interface CategorizableVendiblesResponse {
	public Map<String, CategoryHierarchy> getCategorias();

	public void setCategorias(Map<String, CategoryHierarchy> categorias);
}
