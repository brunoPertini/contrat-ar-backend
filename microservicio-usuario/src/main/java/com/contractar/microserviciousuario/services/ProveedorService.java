package com.contractar.microserviciousuario.services;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciocommons.exceptions.vendibles.CantCreateException;
import com.contractar.microserviciocommons.exceptions.vendibles.SubscriptionAlreadyExistsException;
import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Suscripcion;
import com.contractar.microserviciousuario.repository.PlanRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.SuscripcionRepository;

@Service
public class ProveedorService {

	private PlanRepository planRepository;

	private ProveedorRepository proveedorRepository;

	private SuscripcionRepository suscripcionRepository;
	
	private UsuarioService usuarioService;

	private RestTemplate httpClient;

	@Value("${microservicio-commons.url}")
	private String microservicioCommonsUrl;

	private String fetchDatePattern() {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(microservicioCommonsUrl)
				.path(DateControllerUrls.DATES_BASE_URL).queryParam("operation", DateOperationType.FORMAT)
				.queryParam("format", DateFormatType.FULL);

		return httpClient.getForObject(uriBuilder.toUriString(), String.class);
	}

	public ProveedorService(PlanRepository planRepository, ProveedorRepository proveedorRepository,
			SuscripcionRepository suscripcionRepository, RestTemplate httpClient, UsuarioService usuarioService) {
		this.planRepository = planRepository;
		this.proveedorRepository = proveedorRepository;
		this.suscripcionRepository = suscripcionRepository;
		this.httpClient = httpClient;
		this.usuarioService = usuarioService;
	}

	public Proveedor findById(Long proveedorId) throws UserNotFoundException {

		return proveedorRepository.findById(proveedorId).map(proveedor -> proveedor)
				.orElseThrow(UserNotFoundException::new);
	}

	public List<Plan> findAll() {
		return planRepository.findAll();
	}
	
	public Suscripcion findSuscripcionById(Long id) throws SuscriptionNotFound {
		return this.suscripcionRepository.findById(id)
				.map(s -> s)
				.orElseThrow(() -> new SuscriptionNotFound(""));
	}

	public SuscripcionDTO createSuscripcion(Long proveedorId, Long planId)
			throws UserNotFoundException, CantCreateException {
		Proveedor proveedor = this.findById(proveedorId);

		Plan plan = planRepository.findById(planId).map(p -> p).orElseThrow(CantCreateException::new);
		
		if (suscripcionRepository.existsByUsuario_Id(proveedorId)) {
			throw new SubscriptionAlreadyExistsException(usuarioService.getMessageTag("exceptions.subscription.alreadyCreated"));
		}
		
		boolean isActive = plan.getType().equals(PlanType.FREE);

		Suscripcion suscripcion = new Suscripcion(isActive, proveedor, plan, LocalDate.now());

		suscripcionRepository.save(suscripcion);

		proveedor.setSuscripcion(suscripcion);
		proveedorRepository.save(proveedor);
		
		// TODO: no setearla by default como activa. Si es un plan pago, setearla inactiva para que despues si se completa el primer pago se actualize como activa.

		return new SuscripcionDTO(suscripcion.getId(), isActive, proveedorId, planId, suscripcion.getCreatedDate(), fetchDatePattern());

	}
}
