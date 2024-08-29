package com.contractar.microserviciousuario.helpers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.UsuarioAccesor;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.dto.DateOperationDTO;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuarioAdminDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Suscripcion;
import com.contractar.microserviciousuario.models.Usuario;

@Component
public final class DtoHelper {	
	@Autowired
	private RestTemplate httpClient;
	
	@Value("${microservicio-commons.url}")
	private String microservicioCommonsUrl;

	public static UsuarioDTO toUsuarioDTO(Usuario usuario) {
		return new UsuarioDTO(usuario.getId(), usuario.getName(), usuario.getSurname(), usuario.getEmail(),
				usuario.isActive(), usuario.getBirthDate(), usuario.getLocation(), usuario.getPhone());
	};

	public static ProveedorDTO toProveedorDTO(Proveedor proveedor) {
		Suscripcion usuarioSuscripcion = (Suscripcion) proveedor.getSuscripcion();

		SuscripcionDTO suscripcion = new SuscripcionDTO(usuarioSuscripcion.getId(), usuarioSuscripcion.isActive(),
				usuarioSuscripcion.getUsuario().getId(), usuarioSuscripcion.getPlan().getId(),
				usuarioSuscripcion.getCreatedDate());

		return new ProveedorDTO(proveedor.getId(), proveedor.getName(), proveedor.getSurname(), proveedor.getEmail(),
				proveedor.isActive(), proveedor.getBirthDate(), proveedor.getLocation(), proveedor.getDni(),
				suscripcion, proveedor.getProveedorType(), proveedor.getPhone(), proveedor.getFotoPerfilUrl());
	}

	public ProveedorDTO toProveedorDTO(Proveedor proveedor, DateFormatType dateFormat) {
		LocalDate subscriptionDate = proveedor.getSuscripcion().getCreatedDate();
		DateOperationDTO body = new DateOperationDTO(subscriptionDate,
				DateOperationType.FORMAT,
				dateFormat != null ? dateFormat : DateFormatType.FULL);
		String datePattern = httpClient.postForObject(microservicioCommonsUrl + DateControllerUrls.DATES_BASE_URL, body, String.class);
		return new ProveedorDTO(proveedor, datePattern);
	}

	public static UsuarioAdminDTO toUsuarioAdminDTO(UsuarioAccesor usuario) {
		return new UsuarioAdminDTO(usuario);
	}

	public static ProveedorAdminDTO toProveedorAdminDTO(ProveedorAccessor proveedor) {
		return new ProveedorAdminDTO(proveedor);
	}
}
