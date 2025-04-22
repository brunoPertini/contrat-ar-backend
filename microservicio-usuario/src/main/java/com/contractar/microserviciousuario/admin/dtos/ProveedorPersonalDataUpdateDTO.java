package com.contractar.microserviciousuario.admin.dtos;

import java.time.LocalDate;

import org.locationtech.jts.geom.Point;

public class ProveedorPersonalDataUpdateDTO extends UsuarioPersonalDataUpdateDTO {
	private String dni;
	private String fotoPerfilUrl;

	private Boolean hasWhatsapp;

	public ProveedorPersonalDataUpdateDTO() {
	}

	public ProveedorPersonalDataUpdateDTO(String name, String surname, LocalDate birthDate, String phone,
			Point location, boolean active, String dni, String fotoPerfilUrl) {
		super(name, surname, birthDate, phone, location, active);
		this.dni = dni;
		this.fotoPerfilUrl = fotoPerfilUrl;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getFotoPerfilUrl() {
		return fotoPerfilUrl;
	}

	public void setFotoPerfilUrl(String fotoPerfilUrl) {
		this.fotoPerfilUrl = fotoPerfilUrl;
	}

	public Boolean hasWhatsapp() {
		return hasWhatsapp;
	}

	public void setHasWhatsapp(Boolean hasWhatsapp) {
		this.hasWhatsapp = hasWhatsapp;
	}

}
