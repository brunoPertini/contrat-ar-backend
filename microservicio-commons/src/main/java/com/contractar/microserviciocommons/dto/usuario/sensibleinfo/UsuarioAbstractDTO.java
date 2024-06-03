package com.contractar.microserviciocommons.dto.usuario.sensibleinfo;

public class UsuarioAbstractDTO {
	private Boolean active;
	
	public UsuarioAbstractDTO() {}

	public UsuarioAbstractDTO(boolean isActive) {
		this.active = isActive;
	}

	public boolean isActive() {
		return active;
	}

	public void setIsActive(boolean isActive) {
		this.active = isActive;
	}
	
}
