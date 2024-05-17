package com.contractar.microserviciocommons.dto.usuario;

import org.locationtech.jts.geom.Point;

public class ProveedorInfoUpdateDTO extends UsuarioCommonInfoUpdateDTO {
	private String fotoPerfilUrl;
	
	public ProveedorInfoUpdateDTO(Point location, String phone, String fotoPerfilUrl) {
		super(location, phone);
		this.fotoPerfilUrl = fotoPerfilUrl;
	}
	
	public String getFotoPerfilUrl() {
		return fotoPerfilUrl;
	}

	public void setFotoPerfilUrl(String fotoPerfilUrl) {
		this.fotoPerfilUrl = fotoPerfilUrl;
	}
}
