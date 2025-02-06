package com.contractar.microserviciocommons.dto.usuario.sensibleinfo;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;

public class UsuarioAbstractDTO {
	Long id; // User id
	private Boolean active;
	
	public UsuarioAbstractDTO() {}
	
	public UsuarioAbstractDTO(Long userId, boolean isActive) {
		this.id = userId;
		this.active = isActive;
	}

	public UsuarioAbstractDTO(boolean isActive) {
		this.active = isActive;
	}

	public boolean isActive() {
		return active;
	}

	public void setIsActive(boolean isActive) {
		this.active = isActive;
	}
	
	public Long getUserId() {
		return id;
	}

	public void setUserId(Long userId) {
		this.id = userId;
	}
	
	public static String getChangeDetailUrl(Long userId) {
		return UsersControllerUrls.GET_USUARIO_INFO.replace("{userId}", userId.toString());
	}
	
}
