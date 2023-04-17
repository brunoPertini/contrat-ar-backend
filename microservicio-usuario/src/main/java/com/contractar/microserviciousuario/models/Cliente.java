package com.contractar.microserviciousuario.models;

import java.time.LocalDate;
import org.locationtech.jts.geom.Point;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "clienteId")
public class Cliente extends Usuario {
	private static final long serialVersionUID = -18128319090812613L;

	public Cliente() {}

	public Cliente(Long id, String name, String surname, String email, boolean isActive,
	Point location, String password, LocalDate birthDate) {
		super(id, name, surname, email, isActive, location, birthDate);
	}
}
