package com.contractar.usuarios.models;

import java.awt.Point;

import com.contractar.serviciocommons.plans.PlanType;

public class Proveedor extends Usuario {
	
	private String dni;
	
	private String password;

	public Proveedor(Long id, String nombre, String apellido, String email, boolean isActive,Point ubicacion,
			String dni, String password, PlanType plan) {
		super(id, nombre, apellido, email, isActive, ubicacion, plan);
		this.dni = dni;
		this.password = password;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
