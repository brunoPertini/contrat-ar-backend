package com.contractar.usuarios.models;

import org.locationtech.jts.geom.Point;

import com.contractar.serviciocommons.plans.PlanType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "clienteId")
public class Cliente extends Usuario {
	public Cliente(Long id, String nombre, String apellido, String email, boolean isActive,
			Point ubicacion, String password, PlanType plan) {
		super(id, nombre, apellido, email, isActive, ubicacion);
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
