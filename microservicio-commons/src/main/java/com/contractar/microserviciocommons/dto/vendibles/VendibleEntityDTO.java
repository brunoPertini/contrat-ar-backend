package com.contractar.microserviciocommons.dto.vendibles;

import com.contractar.microserviciovendible.models.VendibleCategory;

public class VendibleEntityDTO implements CategorizableObject {
	private String nombre;
	private VendibleCategory category;
	
	public VendibleEntityDTO() {}

	public VendibleEntityDTO(String nombre, VendibleCategory category) {
		super();
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
	public VendibleCategory getCategory() {
		return category;
	}

	@Override
	public void setCategory(VendibleCategory category) {
		this.category = category;
	}

}
