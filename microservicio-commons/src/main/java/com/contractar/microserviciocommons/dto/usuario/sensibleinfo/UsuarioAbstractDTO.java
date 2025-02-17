package com.contractar.microserviciocommons.dto.usuario.sensibleinfo;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;

public class UsuarioAbstractDTO {
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

	public static String getChangeDetailUrl(Long userId) {
		return UsersControllerUrls.GET_USUARIO_INFO.replace("{userId}", userId.toString());
	}

}
