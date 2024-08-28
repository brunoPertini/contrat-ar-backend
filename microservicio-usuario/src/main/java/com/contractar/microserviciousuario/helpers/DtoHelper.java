package com.contractar.microserviciousuario.helpers;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.UsuarioAccesor;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuarioAdminDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Suscripcion;
import com.contractar.microserviciousuario.models.Usuario;

public final class DtoHelper {
	public static UsuarioDTO toUsuarioDTO(Usuario usuario) {
		return new UsuarioDTO(usuario.getId(), usuario.getName(), usuario.getSurname(), usuario.getEmail(),
				usuario.isActive(), usuario.getBirthDate(), usuario.getLocation(), usuario.getPhone());
	};

	public static ProveedorDTO toProveedorDTO(Proveedor proveedor) {
		Suscripcion usuarioSuscripcion = (Suscripcion) proveedor.getSuscripcion();

		SuscripcionDTO suscripcion = new SuscripcionDTO(
				usuarioSuscripcion.getId(),
				usuarioSuscripcion.isActive(),
				usuarioSuscripcion.getUsuario().getId(),
				usuarioSuscripcion.getPlan().getId(),
				usuarioSuscripcion.getCreatedDate());
	
		return new ProveedorDTO(proveedor.getId(), proveedor.getName(), proveedor.getSurname(), proveedor.getEmail(),
				proveedor.isActive(), proveedor.getBirthDate(), proveedor.getLocation(), proveedor.getDni(),
				suscripcion, proveedor.getProveedorType(), proveedor.getPhone(),
				proveedor.getFotoPerfilUrl());
	}
	
	public static UsuarioAdminDTO toUsuarioAdminDTO(UsuarioAccesor usuario) {
		return new UsuarioAdminDTO(usuario);
	}
	
	public static ProveedorAdminDTO toProveedorAdminDTO(ProveedorAccessor proveedor) {
		return new ProveedorAdminDTO(proveedor);
	}
}
