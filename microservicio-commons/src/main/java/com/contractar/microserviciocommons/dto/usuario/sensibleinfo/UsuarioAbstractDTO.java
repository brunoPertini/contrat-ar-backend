package com.contractar.microserviciocommons.dto.usuario.sensibleinfo;

public abstract class UsuarioAbstractDTO {
	Long id; // User id

	public UsuarioAbstractDTO() {
	}

	public UsuarioAbstractDTO(Long userId) {
		this.id = userId;
	}

	public Long getUserId() {
		return id;
	}

	public void setUserId(Long userId) {
		this.id = userId;
	}

	public abstract String getChangeDetailUrl(Long userId);

}
