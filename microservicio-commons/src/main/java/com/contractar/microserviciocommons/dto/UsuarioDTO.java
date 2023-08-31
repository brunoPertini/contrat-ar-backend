package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

import org.locationtech.jts.geom.Point;

import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciousuario.models.Role;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Class to serialize user info that is not needed to handle session. Others DTOs should inherit from this, as it contains common info.
 *
 */
public class UsuarioDTO {
	@NotBlank
	private String name;
	@NotBlank
	private String surname;
	@NotBlank
	private String email;
	
	private boolean isActive;
	
	@NotNull
	private LocalDate birthDate;

	@OneToOne
	@JoinColumn(name = "role")
	private Role role;
	
	@JsonDeserialize(using = UbicacionDeserializer.class)
	private Point location;
	
	public UsuarioDTO() {}

	public UsuarioDTO(@NotBlank String name, @NotBlank String surname, @NotBlank String email, boolean isActive,
			@NotNull LocalDate birthDate, Role role, Point location) {
		super();
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.isActive = isActive;
		this.birthDate = birthDate;
		this.role = role;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}
}
