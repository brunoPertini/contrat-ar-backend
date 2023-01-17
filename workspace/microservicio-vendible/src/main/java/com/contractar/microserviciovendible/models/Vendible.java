package com.contractar.microserviciovendible.models;

import java.io.Serializable;

public class Vendible implements Serializable {

	private static final long serialVersionUID = -4955873418985503685L;
	
	private int precio;
	
	private String descripcion;

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Vendible() {
		
	}

	public Vendible(int precio, String descripcion) {
		super();
		this.precio = precio;
		this.descripcion = descripcion;
	}

}
