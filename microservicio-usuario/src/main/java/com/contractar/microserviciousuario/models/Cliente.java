package com.contractar.microserviciousuario.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Point;
import org.springframework.security.core.GrantedAuthority;

import com.contractar.microserviciousuario.serialization.ClienteDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "clienteId")
@JsonDeserialize(using = ClienteDeserializer.class)
public class Cliente extends Usuario {
	private static final long serialVersionUID = -18128319090812613L;
	
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<DistanciaProveedor> distances;
	
	public Cliente() {
		super();
	}

	public Cliente(Long id, String name, String surname, String email, boolean isActive,
	Point location, LocalDate birthDate, String password, List<GrantedAuthority> authorities, Role role) {
		super(id, name, surname, email, isActive, location, birthDate, password, authorities, role);
	}
	
	public Set<DistanciaProveedor> getDistances() {
		return distances;
	}

	public void setDistances(Set<DistanciaProveedor> distances) {
		this.distances = distances;
	}

}
