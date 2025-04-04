package com.contractar.microserviciousuario.helpers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.UsuarioAccesor;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuarioAdminDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Suscripcion;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.services.ProveedorService;

@Component
public final class DtoHelper {	
	@Autowired
	private RestTemplate httpClient;
	
	@Autowired
	private ProveedorService proveedorService;
	
	@Value("${microservicio-commons.url}")
	private String microservicioCommonsUrl;

	public static UsuarioDTO toUsuarioDTO(Usuario usuario) {
		return new UsuarioDTO(usuario.getId(), usuario.getName(), usuario.getSurname(), usuario.getEmail(),
				usuario.isActive(), usuario.getBirthDate(), usuario.getLocation(), usuario.getPhone());
	};

	public static ProveedorDTO toProveedorDTO(Proveedor proveedor) {
		Suscripcion usuarioSuscripcion = Optional.ofNullable(proveedor.getSuscripcion())
				.map(s -> (Suscripcion) s)
				.orElse(null);

		SuscripcionDTO suscripcion = usuarioSuscripcion != null ?  new SuscripcionDTO(usuarioSuscripcion.getId(), usuarioSuscripcion.isActive(),
				usuarioSuscripcion.getUsuario().getId(), usuarioSuscripcion.getPlan().getId(),
				usuarioSuscripcion.getCreatedDate()) : null;

		return new ProveedorDTO(proveedor.getId(), proveedor.getName(), proveedor.getSurname(), proveedor.getEmail(),
				proveedor.isActive(), proveedor.getBirthDate(), proveedor.getLocation(), proveedor.getDni(),
				suscripcion, proveedor.getProveedorType(), proveedor.getPhone(), proveedor.getFotoPerfilUrl());
	}

	public ProveedorDTO toProveedorDTO(Proveedor proveedor, DateFormatType dateFormat) {
		 UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(microservicioCommonsUrl)
				 	.path(DateControllerUrls.DATES_BASE_URL)
	                .queryParam("operation", DateOperationType.FORMAT)
	                .queryParam("format", dateFormat != null ? dateFormat : DateFormatType.FULL);
		 
		String datePattern = httpClient.getForObject(uriBuilder.toUriString(), String.class);
		ProveedorDTO proveedorDTO = new ProveedorDTO(proveedor, datePattern);
		proveedorDTO.setSuscripcion(proveedorService.getSuscripcion(proveedor.getId()));
		
		return proveedorDTO;
	}

	public static UsuarioAdminDTO toUsuarioAdminDTO(UsuarioAccesor usuario) {
		return new UsuarioAdminDTO(usuario);
	}

	public static ProveedorAdminDTO toProveedorAdminDTO(ProveedorAccessor proveedor) {
		return new ProveedorAdminDTO(proveedor);
	}
}
