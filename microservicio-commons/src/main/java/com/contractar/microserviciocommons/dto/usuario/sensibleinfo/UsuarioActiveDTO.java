package com.contractar.microserviciocommons.dto.usuario.sensibleinfo;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;

public class UsuarioActiveDTO extends UsuarioAbstractDTO {
	private boolean active;

	public UsuarioActiveDTO(Long userId, boolean active) {
		super(userId);
		this.active = active;
	}

	public UsuarioActiveDTO(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String getChangeDetailUrl(Long userId) {
		return UsersControllerUrls.GET_USUARIO_INFO.replace("{userId}", userId.toString());
	}

}
