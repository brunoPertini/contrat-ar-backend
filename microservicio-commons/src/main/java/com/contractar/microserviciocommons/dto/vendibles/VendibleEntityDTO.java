package com.contractar.microserviciocommons.dto.vendibles;

import com.contractar.microservicioadapter.entities.VendibleCategoryAccesor;

public class VendibleEntityDTO implements CategorizableObject {
	private String nombre;
	private VendibleCategoryAccesor category;
	
	public VendibleEntityDTO() {}

	public VendibleEntityDTO(String nombre, VendibleCategoryAccesor category) {
		this.nombre = nombre;
		this.category = category;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public VendibleCategoryAccesor getCategory() {
		return category;
	}

	@Override
	public void setCategory(VendibleCategoryAccesor category) {
		this.category = category;
	}

}
