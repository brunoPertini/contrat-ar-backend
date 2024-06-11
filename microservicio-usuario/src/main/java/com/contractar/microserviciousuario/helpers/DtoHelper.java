package com.contractar.microserviciousuario.helpers;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.UsuarioAccesor;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuarioAdminDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;

public final class DtoHelper {
	public static UsuarioDTO toUsuarioDTO(Usuario usuario) {
		return new UsuarioDTO(usuario.getId(), usuario.getName(), usuario.getSurname(), usuario.getEmail(),
				usuario.isActive(), usuario.getBirthDate(), usuario.getLocation(), usuario.getPhone());
	};

	public static ProveedorDTO toProveedorDTO(Proveedor proveedor) {
		return new ProveedorDTO(proveedor.getId(), proveedor.getName(), proveedor.getSurname(), proveedor.getEmail(),
				proveedor.isActive(), proveedor.getBirthDate(), proveedor.getLocation(), proveedor.getDni(),
				proveedor.getPlan().getType(), proveedor.getProveedorType(), proveedor.getPhone(),
				proveedor.getFotoPerfilUrl());
	}
	
	public static UsuarioAdminDTO toUsuarioAdminDTO(UsuarioAccesor usuario) {
		return new UsuarioAdminDTO(usuario);
	}
	
	public static ProveedorAdminDTO toProveedorAdminDTO(ProveedorAccessor proveedor) {
		return new ProveedorAdminDTO(proveedor);
	}
}
