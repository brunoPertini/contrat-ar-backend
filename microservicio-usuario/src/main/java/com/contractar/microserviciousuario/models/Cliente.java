package com.contractar.microserviciousuario.models;

import java.time.LocalDate;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "clienteId")
public class Cliente extends Usuario {
	private static final long serialVersionUID = -18128319090812613L;
	
	public Cliente() {
		super();
	}

	public Cliente(Long id, String name, String surname, String email, boolean isActive,
	Point location, LocalDate birthDate, String password, List<GrantedAuthority> authorities, Role role) {
		super(id, name, surname, email, isActive, location, birthDate, password, authorities, role);
	}
}
