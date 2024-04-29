package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

import org.locationtech.jts.geom.Point;

import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Class to serialize user info that is not needed to handle session. Others
 * DTOs should inherit from this, as it contains common info.
 *
 */
public class UsuarioDTO {
	private Long id;

	private String name;
	private String surname;
	private String email;

	private boolean isActive;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate birthDate;
	
	private String password;

	@JsonDeserialize(using = UbicacionDeserializer.class)
	@JsonSerialize(using = UbicacionSerializer.class)
	private Point location;

	public UsuarioDTO() {
	}

	public UsuarioDTO(String name, String surname, String email, boolean isActive, LocalDate birthDate,
			Point location, String password) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.isActive = isActive;
		this.birthDate = birthDate;
		this.location = location;
		this.password = password;
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

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
