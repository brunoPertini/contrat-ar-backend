package com.contractar.microserviciousuario.admin.dtos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;

public class UsuariosByTypeResponse {
	private Map<String, List<? extends UsuarioDTO>> usuarios;
	

	public UsuariosByTypeResponse() {
		this.usuarios = new HashMap<>();
	}
	
	public UsuariosByTypeResponse(Map<String, List<? extends UsuarioDTO>> usuarios) {
		this.usuarios = usuarios;
	}
	
	public Map<String, List<? extends UsuarioDTO>> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Map<String, List<? extends UsuarioDTO>> usuarios) {
		this.usuarios = usuarios;
	}
	
	
}
