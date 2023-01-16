package com.contractar.usuarios.models;

import org.locationtech.jts.geom.Point;
import com.contractar.serviciocommons.plans.PlanType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedor")
public class Proveedor extends Usuario {
	
	@Column(length = 80, unique = true)
	private String dni;
	
	@Column(length = 40)
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
