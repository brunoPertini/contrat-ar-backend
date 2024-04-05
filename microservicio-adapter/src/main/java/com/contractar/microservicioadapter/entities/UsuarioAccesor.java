package com.contractar.microservicioadapter.entities;

import java.time.LocalDate;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.enums.RoleAccesor;

public interface UsuarioAccesor {
	public Long getId();

	public String getname();

	public String getsurname();

	public String getEmail();

	public boolean isActive();

	public Point getlocation();

	public LocalDate getBirthDate();

	public String getPassword();

	public RoleAccesor getRole();

}
