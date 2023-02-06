package com.contractar.microserviciousuario.models;

import org.locationtech.jts.geom.Point;

import com.contractar.serviciocommons.plans.PlanType;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "clienteId")
public class Cliente extends Usuario {
	private static final long serialVersionUID = -18128319090812613L;

	public Cliente(Long id, String nombre, String apellido, String email, boolean isActive,
			Point ubicacion, String password, PlanType plan) {
		super(id, nombre, apellido, email, isActive, ubicacion);
	}
}
