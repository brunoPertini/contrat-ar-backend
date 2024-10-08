package com.contractar.microserviciocommons.dto.usuario;

import java.time.LocalDate;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.entities.UsuarioAccesor;
import com.contractar.microservicioadapter.enums.RoleAccesor;
import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
	
	private String phone;

	@JsonDeserialize(using = UbicacionDeserializer.class)
	@JsonSerialize(using = UbicacionSerializer.class)
	private Point location;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private RoleAccesor role;
	
	private String creationToken;

	public UsuarioDTO() {
	}
	
	public UsuarioDTO(UsuarioAccesor u) {
		this.id = u.getId();
		this.name = u.getName();
		this.surname = u.getSurname();
		this.email = u.getEmail();
		this.isActive = u.isActive();
		this.birthDate = u.getBirthDate();
		this.location = u.getLocation();
		this.phone = u.getPhone();
	}

	public UsuarioDTO(Long id, String name, String surname, String email, boolean isActive, LocalDate birthDate,
			Point location, String phone) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.isActive = isActive;
		this.birthDate = birthDate;
		this.location = location;
		this.phone = phone;
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
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
	
	public RoleAccesor getRole() {
		return role;
	}

	public void setRole(RoleAccesor role) {
		this.role = role;
	}
	
	public String getCreationToken() {
		return creationToken;
	}

	public void setCreationToken(String creationToken) {
		this.creationToken = creationToken;
	}

}
