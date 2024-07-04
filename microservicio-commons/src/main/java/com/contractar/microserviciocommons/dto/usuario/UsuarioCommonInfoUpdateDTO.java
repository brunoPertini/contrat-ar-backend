package com.contractar.microserviciocommons.dto.usuario;

import org.locationtech.jts.geom.Point;

/**
 * Info that can be edited by any type of user
 */
public class UsuarioCommonInfoUpdateDTO {

	// TODO: ver porque el deserializador custom no funciona
	private Point location;
	
	private String phone;
	
	public UsuarioCommonInfoUpdateDTO() {}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public UsuarioCommonInfoUpdateDTO(Point location, String phone) {
		this.location = location;
		this.phone = phone;
	}
}
