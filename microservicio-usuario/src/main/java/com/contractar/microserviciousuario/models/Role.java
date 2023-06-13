package com.contractar.microserviciousuario.models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Role implements Serializable{

	@Id
	private String nombre;

	private static final long serialVersionUID = 1L;
	
	public Role() {}
	
	public Role(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}