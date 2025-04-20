package com.contractar.microserviciousuario.admin.services;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.RolesNames.RolesValues;
import com.contractar.microserviciocommons.constants.controllers.AdminControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.ProveedorControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.dto.UsuarioFiltersDTO;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleUpdateDTO;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioActiveDTO;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioSensibleInfoDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.OperationNotAllowedException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.mailing.AdminChangeRequestInfo;
import com.contractar.microserviciocommons.mailing.MailInfo;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciousuario.admin.controllers.AdminController.UsuariosTypeFilter;
import com.contractar.microserviciousuario.admin.dtos.ProveedorAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorPersonalDataUpdateDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorVendibleAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuarioPersonalDataUpdateDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuariosByTypeResponse;
import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.repositories.ChangeRequestRepository;
import com.contractar.microserviciousuario.admin.repositories.ChangeRequestRepositoryImpl;
import com.contractar.microserviciousuario.admin.repositories.UsuarioAdminCustomRepository;
import com.contractar.microserviciousuario.admin.utils.ChangeRequestFactoryStrategy;
import com.contractar.microserviciousuario.admin.utils.ChangeRequestStrategy;
import com.contractar.microserviciousuario.helpers.DtoHelper;
import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.repository.ClienteRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.UsuarioRepository;
import com.contractar.microserviciousuario.services.ProveedorVendibleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class AdminService {
	@Autowired
	private ProveedorVendibleService proveedorVendibleService;

	@Autowired
	private ChangeRequestRepository repository;

	@Autowired
	private ChangeRequestRepositoryImpl repositoryImpl;

	@Autowired
	private UsuarioAdminCustomRepository usuarioAdminCustomRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ProveedorRepository proveedorRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ChangeRequestRepository changeRequestRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${microservicio-config.url}")
	private String serviceConfigUrl;

	@Value("${microservicio-security.url}")
	private String serviceSecurityUrl;

	@Value("${microservicio-mailing.url}")
	private String serviceMailingUrl;

	private final String USER_NOT_FOUND_MESSAGE = "Usuario no encontrado";

	// TODO: refactor this, should not be hardcoded, should not be aware of DDBB
	// attributes names
	private final List<String> userEntitiedIdsNames = List.of("proveedor_id", "cliente_id", "id");

	private final Map<String, Function<Long, ? extends Usuario>> fetchEntity = Map
			.of(UsuariosTypeFilter.clientes.name(), (clienteId) -> {
				try {
					return clienteRepository.findById(clienteId).map(cliente -> cliente)
							.orElseThrow(() -> new UserNotFoundException());
				} catch (UserNotFoundException e) {
					throw new RuntimeException(e);
				}

			}, UsuariosTypeFilter.proveedores.name(), (proveedorId) -> {
				try {
					return proveedorRepository.findById(proveedorId).map(proveedor -> proveedor)
							.orElseThrow(() -> new UserNotFoundException());
				} catch (UserNotFoundException e) {
					throw new RuntimeException(e);
				}

			});

	private String getMessageTag(String tagId) {
		final String fullUrl = serviceConfigUrl + "/i18n/" + tagId;
		return restTemplate.getForObject(fullUrl, String.class);
	}

	public void sendEmail(String path, MailInfo body) {
		restTemplate.postForEntity(serviceMailingUrl + path, body, Void.class);
	}

	private void sendChangeRequestNotificationEmails(ChangeRequest changeRequest) {
		usuarioRepository.findAllByRoleNombre(RolesValues.ADMIN.name()).stream().forEach(user -> {
			sendEmail(AdminControllerUrls.ADMIN_SEND_NEW_CHANGE_REQUEST_EMAIL,
					new AdminChangeRequestInfo(user.getEmail(), changeRequest.getId(), changeRequest.getSourceTable()));
		});
	}

	public Object getUserPayloadFromToken(HttpServletRequest request) {
		String getPayloadUrl = serviceSecurityUrl + SecurityControllerUrls.GET_USER_PAYLOAD_FROM_TOKEN;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", request.getHeader("Authorization"));

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> getPayloadResponse = restTemplate.exchange(getPayloadUrl, HttpMethod.GET, entity,
				Object.class);

		return getPayloadResponse.getBody();
	}

	public List<ChangeRequest> findAllChangeRequests() {
		return repository.findAll();
	}

	public Usuario findUserById(Long id) throws UserNotFoundException {
		return usuarioRepository.findById(id).map(u -> u).orElseThrow(UserNotFoundException::new);
	}

	public UsuarioSensibleInfoDTO findUserSensibleInfo(Long userId) throws UserNotFoundException {
		Usuario usuario = findUserById(userId);
		return new UsuarioSensibleInfoDTO(usuario.getEmail(), usuario.getPassword());
	}

	public ProveedorVendible findPost(ProveedorVendibleId id) throws VendibleNotFoundException {
		return this.proveedorVendibleService.findById(id);
	}

	public Long getMatchingChangeRequest(List<Long> sourceTableIds, List<String> attributes) {
		String idsAsString = sourceTableIds.size() > 1
				? Helper.joinString.apply(sourceTableIds.stream().map(id -> id.toString()).collect(Collectors.toList()))
				: sourceTableIds.get(0).toString();

		String attributesAsString = attributes.size() > 1 ? Helper.joinString.apply(attributes)
				: attributes.get(0).toString();

		return repository.getMatchingChangeRequest(idsAsString, attributesAsString);
	}

	public void addChangeRequestEntry(UsuarioActiveDTO info) throws ChangeAlreadyRequestedException {
		String concatenatedIds = info.getUserId().toString();
		boolean alreadyRequested = repository.getMatchingChangeRequest(concatenatedIds, "active") != null;

		if (alreadyRequested) {
			throw new ChangeAlreadyRequestedException(getMessageTag("exceptions.change.already.requested"));
		}

		ChangeRequest newRequest = new ChangeRequest("usuario", "active=true", false, List.of(info.getUserId()),
				List.of("id"));

		newRequest.setChangeDetailUrl(info.getChangeDetailUrl(info.getUserId()));
		ChangeRequest createdChangeRequest = repository.save(newRequest);

		sendChangeRequestNotificationEmails(createdChangeRequest);
	}

	public void addChangeRequestEntry(ProveedorVendibleAdminDTO newInfo, Long proveedorId, Long vendibleId)
			throws IllegalAccessException, ChangeAlreadyRequestedException {
		String concatenatedIds = proveedorId.toString() + "," + vendibleId.toString();
		boolean alreadyRequested = repository.getMatchingChangeRequest(concatenatedIds, "state") != null;

		if (alreadyRequested) {
			throw new ChangeAlreadyRequestedException(getMessageTag("exceptions.change.already.requested"));
		}

		// Only state should be approved by an admin, the other attributes can be
		// changed by the proveedor
		if (newInfo.getState() != null) {
			ChangeRequest newRequest = new ChangeRequest("proveedor_vendible", "state='" + newInfo.getState() + "'",
					false, List.of(proveedorId, vendibleId), List.of("proveedor_id", "vendible_id"));

			newRequest.setChangeDetailUrl(ProveedorVendibleAdminDTO.getDTODetailUrl(proveedorId, vendibleId));
			ChangeRequest changeRequest = repository.save(newRequest);
			sendChangeRequestNotificationEmails(changeRequest);

		}
	}

	public void addChangeRequestEntry(UsuarioPersonalDataUpdateDTO newInfo, List<String> sourceTableIds)
			throws IllegalAccessException, ChangeAlreadyRequestedException {
		HashMap<String, Object> infoAsMap = (HashMap<String, Object>) ReflectionHelper.getObjectFields(newInfo);
		String concatenatedIds = Helper.joinString.apply(sourceTableIds);

		boolean someInfoAlreadyRequested = infoAsMap.keySet().stream().anyMatch(newInfoKey -> {
			Long matchingRequest = repository.getMatchingChangeRequest(concatenatedIds, newInfoKey);
			return matchingRequest != null;
		});

		if (someInfoAlreadyRequested) {
			throw new ChangeAlreadyRequestedException();
		}

		StringBuilder attributesBuilder = new StringBuilder("");

		infoAsMap.forEach((key, value) -> {
			if (value != null) {
				// TODO: This is to not add unnecessary ' when applying the UPDATE operation
				// later. Refactor it
				String formattedValue = value instanceof String ? '\'' + value.toString() + '\'' : value.toString();
				attributesBuilder.append(key).append("=").append(formattedValue).append(",");
			}
		});

		if (!attributesBuilder.isEmpty()) {
			attributesBuilder.deleteCharAt(attributesBuilder.length() - 1);
			ChangeRequest newRequest = new ChangeRequest("usuario", attributesBuilder.toString(), true,
					sourceTableIds.stream().map(Long::parseLong).collect(Collectors.toList()), List.of("id"));

			boolean isChangingPasswordOrEmail = Optional.ofNullable(newInfo.getPassword()).isPresent()
					|| Optional.ofNullable(newInfo.getEmail()).isPresent();

			if (!isChangingPasswordOrEmail) {
				newRequest.setChangeDetailUrl(newInfo.getChangeDetailUrl(newInfo.getUserId()));
			} else {
				UsuarioSensibleInfoDTO sensibleInfoDTO = new UsuarioSensibleInfoDTO(newInfo.getEmail(),
						newInfo.getPassword());
				sensibleInfoDTO.setUserId(newInfo.getUserId());
				newRequest.setChangeDetailUrl(sensibleInfoDTO.getChangeDetailUrl(sensibleInfoDTO.getUserId()));
			}

			ChangeRequest changeRequest = repository.save(newRequest);
			sendChangeRequestNotificationEmails(changeRequest);
		}

	}

	public void addChangeRequestEntry(Long proveedorId, Long subscriptionId) {
		proveedorRepository.findById(proveedorId).ifPresentOrElse(foundProveedor -> {
			try {
				boolean infoAlreadyRequested = getMatchingChangeRequest(List.of(proveedorId),
						List.of(subscriptionId.toString())) != null;

				if (infoAlreadyRequested) {
					throw new ChangeAlreadyRequestedException();
				}

				String planAttributeChangeQuery = "suscripcion=" + "\'" + subscriptionId.toString() + "\'";

				ChangeRequest planChangeRequest = new ChangeRequest("proveedor", planAttributeChangeQuery, false,
						List.of(proveedorId), List.of("proveedor_id"));

				planChangeRequest.setChangeDetailUrl(
						ProveedorControllerUrls.GET_SUSCRIPCION.replace("{suscriptionId}", subscriptionId.toString()));
				ChangeRequest savedChangeRequest = repository.save(planChangeRequest);
				sendChangeRequestNotificationEmails(savedChangeRequest);
				
			} catch (ChangeAlreadyRequestedException e) {
				throw new RuntimeException(e);
			}
		}, () -> {
			try {
				throw new ChangeConfirmException();
			} catch (ChangeConfirmException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void performPostUpdate(ProveedorVendible post, ProveedorVendibleAdminDTO newInfo)
			throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		ReflectionHelper.applySetterFromExistingFields(newInfo, post, ReflectionHelper.getObjectClassFullName(newInfo),
				ReflectionHelper.getObjectClassFullName(post));
		proveedorVendibleService.save(post);
	}

	public void updatePostAdmin(ProveedorVendibleAdminDTO newInfo, Long proveedorId, Long vendibleId,
			HttpServletRequest request)
			throws VendibleNotFoundException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, ChangeAlreadyRequestedException {
		Map<String, Object> tokenPayload = (Map<String, Object>) proveedorVendibleService
				.getUserPayloadFromToken(request);

		String role = (String) tokenPayload.get("role");

		ProveedorVendible post = proveedorVendibleService.findById(new ProveedorVendibleId(proveedorId, vendibleId));

		if (role.startsWith("ADMIN")) {
			performPostUpdate(post, newInfo);
		} else {
			Optional.ofNullable(newInfo.getState()).ifPresentOrElse((state) -> {
				boolean canUpdateStraight = proveedorVendibleService.canUpdatePostStateChange(post,
						new ProveedorVendibleUpdateDTO(state));

				if (canUpdateStraight) {
					try {
						performPostUpdate(post, newInfo);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

				} else {
					try {
						addChangeRequestEntry(newInfo, proveedorId, vendibleId);
					} catch (IllegalAccessException | ChangeAlreadyRequestedException e) {
						throw new RuntimeException(e);
					}
				}

			}, () -> {
				throw new OperationNotAllowedException(getMessageTag("exceptions.operation.not.allowed"));
			});
		}
	}

	private ChangeRequest findById(Long id) throws ChangeConfirmException {
		Optional<ChangeRequest> requestOpt = repository.findById(id);

		if (requestOpt.isEmpty()) {
			throw new ChangeConfirmException();
		}

		return requestOpt.get();
	}

	public void confirmChangeRequest(Long id) throws ChangeConfirmException {

		ChangeRequest change = this.findById(id);

		final Map<String, Supplier<ChangeRequestStrategy>> creators = Map.of("usuario",
				ChangeRequestFactoryStrategy::createUserAcceptedStrategy, "proveedor_vendible",
				ChangeRequestFactoryStrategy::createPostAcceptedStrategy);

		Optional.ofNullable(creators.get(change.getSourceTable()))
				.ifPresent(strategy -> strategy.get().run(change, this));

		repositoryImpl.applyChangeRequest(change);
	}

	public void deleteChangeRequest(Long id) {
		this.changeRequestRepository.deleteById(id);
	}

	public void denyChangeRequest(Long id) throws ChangeConfirmException {
		ChangeRequest request = this.findById(id);

		final Map<String, Supplier<ChangeRequestStrategy>> creators = Map.of("usuario",
				ChangeRequestFactoryStrategy::createUserRejectedStrategy, "proveedor_vendible",
				ChangeRequestFactoryStrategy::createPostRejectedStrategy);

		Optional.ofNullable(creators.get(request.getSourceTable()))
				.ifPresent(denyStrategy -> denyStrategy.get().run(request, this));

		this.deleteChangeRequest(request.getId());

	}

	public void denyPlanChange(Long changeRequestId, HttpServletRequest request) throws ChangeConfirmException {
		@SuppressWarnings("unchecked")
		Map<String, Object> tokenPayload = (Map<String, Object>) this.getUserPayloadFromToken(request);

		String userRole = (String) tokenPayload.get("role");

		// If requesting user is not admin, have to check that logued one matches with
		// the one that requested this change
		if (!userRole.equals(RolesValues.ADMIN.name())) {
			Long loguedUserId = Long.valueOf((String) tokenPayload.get("id"));

			ChangeRequest changeRequest = this.findById(changeRequestId);

			List<String> sourceTableIdNames = changeRequest.getSourceTableIdNames();

			int proveedorIdIndex = IntStream.range(0, sourceTableIdNames.size())
					.filter(i -> sourceTableIdNames.get(i).equals(userEntitiedIdsNames.get(0))).findFirst().orElse(-1);

			if (proveedorIdIndex == -1
					|| !changeRequest.getSourceTableIds().get(proveedorIdIndex).equals(loguedUserId)) {
				throw new ChangeConfirmException();
			}
		}

		this.denyChangeRequest(changeRequestId);
	}

	public UsuariosByTypeResponse getAllFilteredUsuarios(@NonNull String usuarioType, UsuarioFiltersDTO filters,
			@Nullable Boolean showOnlyActives, Long planId) throws IllegalAccessException {
		UsuariosByTypeResponse response = new UsuariosByTypeResponse();

		List<? extends Usuario> filteredUsuarios = usuarioAdminCustomRepository.getFilteredUsuarios(usuarioType,
				filters, showOnlyActives, planId);

		if (usuarioType.equals("proveedores")) {
			response.getUsuarios().put("proveedores", filteredUsuarios.stream()
					.map(u -> new ProveedorAdminDTO((Proveedor) u)).collect(Collectors.toList()));
		} else {
			response.getUsuarios().put("clientes",
					filteredUsuarios.stream().map(DtoHelper::toUsuarioAdminDTO).collect(Collectors.toList()));
		}

		return response;
	}

	public void updateClientePersonalData(Long userId, UsuarioPersonalDataUpdateDTO newInfo)
			throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Cliente entity = (Cliente) fetchEntity.get(UsuariosTypeFilter.clientes.name()).apply(userId);
		String entityClassFullName = ReflectionHelper.getObjectClassFullName(entity);
		String clienteDtoClassFullName = ReflectionHelper.getObjectClassFullName(newInfo);
		ReflectionHelper.applySetterFromExistingFields(newInfo, entity, clienteDtoClassFullName, entityClassFullName);
		clienteRepository.save(entity);

	}

	public void updateProveedorPersonalData(Long userId, ProveedorPersonalDataUpdateDTO newInfo)
			throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Proveedor entity = (Proveedor) fetchEntity.get(UsuariosTypeFilter.proveedores.name()).apply(userId);
		String entityClassFullName = ReflectionHelper.getObjectClassFullName(entity);
		String proveedorDtoClassFullName = ReflectionHelper.getObjectClassFullName(newInfo);
		ReflectionHelper.applySetterFromExistingFields(newInfo, entity, proveedorDtoClassFullName, entityClassFullName);
		proveedorRepository.save(entity);

	}
	
	@Transactional
	public void changeIsUserActive(UsuarioActiveDTO dto) {
		usuarioRepository.findById(dto.getUserId()).ifPresent(user -> {
			user.setActive(dto.isActive());
			usuarioRepository.save(user);
		});
	}

	public void deleteUser(Long userId) throws UserNotFoundException {
		Optional<Usuario> foundOpt = usuarioRepository.findById(userId);

		if (foundOpt.isEmpty()) {
			throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
		}

		Usuario found = foundOpt.get();

		String roleName = found.getRole().getNombre();

		if (roleName.equals(RolesValues.ADMIN.toString())) {
			throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
		}

		if (roleName.equals(RolesValues.CLIENTE.toString())) {
			clienteRepository.deleteById(userId);
		} else {
			proveedorRepository.deleteById(userId);
		}
	}

	private final class Helper {
		static Function<List<String>, String> joinString = (inputList) -> inputList.stream().reduce("",
				(acum, attribute) -> acum + "," + attribute);
	}
}
