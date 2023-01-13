package com.contractar.usuarios.models;

import java.awt.Point;

import com.contractar.serviciocommons.plans.PlanType;

public class Cliente extends Usuario {
	public Cliente(Long id, String nombre, String apellido, String email, boolean isActive,
			Point ubicacion, String password, PlanType plan) {
		super(id, nombre, apellido, email, isActive, ubicacion, plan);
		this.password = password;
	}

	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
