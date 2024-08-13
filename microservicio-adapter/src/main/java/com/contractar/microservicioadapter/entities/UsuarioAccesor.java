package com.contractar.microservicioadapter.entities;

import java.time.LocalDate;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.enums.RoleAccesor;

public interface UsuarioAccesor {
	public Long getId();

	public String getName();

	public String getSurname();

	public String getEmail();

	public boolean isActive();

	public Point getLocation();

	public LocalDate getBirthDate();

	public String getPassword();

	public String getPhone();

	public RoleAccesor getRole();
	
	public LocalDate getCreatedAt();

}
