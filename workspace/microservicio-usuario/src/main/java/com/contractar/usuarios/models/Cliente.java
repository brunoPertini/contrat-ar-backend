package com.contractar.usuarios.models;

import org.locationtech.jts.geom.Point;

import com.contractar.serviciocommons.plans.PlanType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {
	public Cliente(Long id, String nombre, String apellido, String email, boolean isActive,
			Point ubicacion, String password, PlanType plan) {
		super(id, nombre, apellido, email, isActive, ubicacion, plan);
		this.password = password;
	}

	@Column(length = 40)
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
