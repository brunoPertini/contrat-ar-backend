package com.contractar.microserviciousuario.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.UsuarioAccesor;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.dto.UserPromotionDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuarioAdminDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.services.PromotionService;
import com.contractar.microserviciousuario.services.SuscriptionService;

@Component
public final class DtoHelper {	
	private RestTemplate httpClient;
	
	private SuscriptionService suscriptionService;
	
	private PromotionService promotionService;
	
	@Value("${microservicio-commons.url}")
	private String microservicioCommonsUrl;
	
	public DtoHelper(RestTemplate httpClient, SuscriptionService suscriptionService, PromotionService promotionService) {
		this.httpClient = httpClient;
		this.suscriptionService = suscriptionService;
		this.promotionService = promotionService;
	}

	public static UsuarioDTO toUsuarioDTO(Usuario usuario) {
		return new UsuarioDTO(usuario.getId(), usuario.getName(), usuario.getSurname(), usuario.getEmail(),
				usuario.isActive(), usuario.getBirthDate(), usuario.getLocation(), usuario.getPhone());
	}

	public ProveedorDTO toProveedorDTO(Proveedor proveedor) {
		ProveedorDTO proveedorDTO = new ProveedorDTO(proveedor);
		proveedorDTO.setSuscripcion(suscriptionService.getSuscripcion(proveedor.getId()));
		
		UserPromotionDTO promotionInfo = promotionService.findUserPromotion(proveedorDTO.getSuscripcion().getId());
		proveedorDTO.getSuscripcion().setPromotionInfo(promotionInfo);
		
		return proveedorDTO;
	}

	public ProveedorDTO toProveedorDTO(Proveedor proveedor, DateFormatType dateFormat) {
		 UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(microservicioCommonsUrl)
				 	.path(DateControllerUrls.DATES_BASE_URL)
	                .queryParam("operation", DateOperationType.FORMAT)
	                .queryParam("format", dateFormat != null ? dateFormat : DateFormatType.FULL);
		 
		String datePattern = httpClient.getForObject(uriBuilder.toUriString(), String.class);
		ProveedorDTO proveedorDTO = toProveedorDTO(proveedor);
		proveedorDTO.getSuscripcion().setDatePattern(datePattern);
		
		return proveedorDTO;
	}

	public static UsuarioAdminDTO toUsuarioAdminDTO(UsuarioAccesor usuario) {
		return new UsuarioAdminDTO(usuario);
	}

	public static ProveedorAdminDTO toProveedorAdminDTO(ProveedorAccessor proveedor) {
		return new ProveedorAdminDTO(proveedor);
	}
}
