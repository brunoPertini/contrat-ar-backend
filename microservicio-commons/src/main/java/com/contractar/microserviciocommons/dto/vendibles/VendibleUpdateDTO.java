package com.contractar.microserviciocommons.dto.vendibles;

public class VendibleUpdateDTO {
	private String nombre;
	
	public VendibleUpdateDTO() {}
	
	public VendibleUpdateDTO(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}	

}
