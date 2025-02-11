package com.contractar.microserviciousuario.services;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.Role;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.models.Vendible;
import com.contractar.microserviciousuario.repository.ClienteRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.RoleRepository;
import com.contractar.microserviciousuario.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import com.contractar.microservicioadapter.entities.VendibleAccesor;
import com.contractar.microserviciocommons.constants.RolesNames.RolesValues;
import com.contractar.microserviciocommons.constants.controllers.AdminControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.ImagenesControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.usuario.ProveedorInfoUpdateDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioCommonInfoUpdateDTO;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioAbstractDTO;
import com.contractar.microserviciocommons.exceptions.AccountVerificationException;
import com.contractar.microserviciocommons.exceptions.CustomException;
import com.contractar.microserviciocommons.exceptions.ImageNotUploadedException;
import com.contractar.microserviciocommons.exceptions.UserCreationException;
import com.contractar.microserviciocommons.exceptions.UserInactiveException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleBindingException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;
import com.contractar.microserviciocommons.mailing.MailInfo;
import com.contractar.microserviciocommons.mailing.RegistrationLinkMailInfo;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UsuarioService {
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ProveedorRepository proveedorRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RestTemplate httpClient;

	@Autowired
	private ProveedorVendibleService proveedorVendibleService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Value("${microservicio-vendible.url}")
	private String microservicioVendibleUrl;

	@Value("${openstreet-api.url}")
	private String openStreetAPIUrl;

	@Value("${microservicio-imagenes.url}")
	private String microservicioImagenesUrl;

	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;

	@Value("${microservicio-config.url}")
	private String serviceConfigUrl;

	@Value("${microservicio-security.url}")
	private String serviceSecurityUrl;

	@Value("${microservicio-mailing.url}")
	private String mailingServiceUrl;

	public String getMessageTag(String tagId) {
		final String fullUrl = serviceConfigUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}

	public boolean checkUserToken(String token) {
		UriComponentsBuilder tokenCheckUrlBuilder = UriComponentsBuilder.fromHttpUrl(serviceSecurityUrl)
				.path(SecurityControllerUrls.TOKEN_BASE_PATH).queryParam("token", token);

		return httpClient.getForObject(tokenCheckUrlBuilder.toUriString(), Boolean.class);
	}

	private void requestUsuarioActiveFlag(Long userId) throws UserCreationException {
		String url = microservicioUsuarioUrl
				+ AdminControllerUrls.ADMIN_USUARIOS_BY_ID.replace("{id}", userId.toString());
		try {
			httpClient.put(url, new UsuarioAbstractDTO(userId, true));
		} catch (RestClientException e) {
			throw new UserCreationException();
		}
	}

	private String getNewUserToken(String email, Usuario foundUser) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serviceSecurityUrl)
				.path(SecurityControllerUrls.GET_TOKEN_FOR_LINK).queryParam("email", email);

		String linkToken = httpClient.getForObject(uriBuilder.toUriString(), String.class);

		foundUser.setAccountVerificationToken(linkToken);
		this.usuarioRepository.save(foundUser);

		return linkToken;
	}
	
	public String getTokenForCreatedUser(String email, Long userId) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serviceSecurityUrl)
				.path(SecurityControllerUrls.GET_TOKEN_FOR_NEW_USER)
				.queryParam("userId", userId)
				.queryParam("email", email);

		return httpClient.getForObject(uriBuilder.toUriString(), String.class);
	}

	public Usuario create(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	@Transactional(rollbackOn = { UserCreationException.class })
	public Proveedor createProveedor(Proveedor proveedor) throws UserCreationException {
		String roleName = "PROVEEDOR_" + proveedor.getProveedorType().toString();
		Optional<Role> roleOpt = roleRepository.findByNombre(roleName);
		if (roleOpt.isPresent()) {
			proveedor.setRole(roleOpt.get());
			proveedor.setCreatedAt(LocalDate.now());
			proveedor.setPassword(passwordEncoder.encode(proveedor.getPassword()));
			Proveedor newProveedor = proveedorRepository.save(proveedor);
			requestUsuarioActiveFlag(newProveedor.getId());
			return newProveedor;
		}
		throw new UserCreationException();

	}

	@Transactional(rollbackOn = { UserCreationException.class })
	public Cliente createCliente(Cliente cliente) throws UserCreationException {
		Role clienteRole = roleRepository.findByNombre(RolesValues.CLIENTE.toString()).get();
		cliente.setRole(clienteRole);
		cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
		Cliente newCliente = clienteRepository.save(cliente);
		newCliente.setCreatedAt(LocalDate.now());
		requestUsuarioActiveFlag(newCliente.getId());
		return newCliente;
	}

	public Cliente updateCliente(Long clienteId, UsuarioCommonInfoUpdateDTO newInfo) throws Exception {
		Optional<Cliente> clienteOpt = this.clienteRepository.findById(clienteId);

		if (!clienteOpt.isPresent()) {
			throw new UserNotFoundException("El usuario no existe");
		}

		Cliente cliente = clienteOpt.get();

		String dtoFullClassName = UsuarioCommonInfoUpdateDTO.class.getPackage().getName()
				+ ".UsuarioCommonInfoUpdateDTO";
		String entityFullClassName = Cliente.class.getPackage().getName() + ".Cliente";

		try {
			ReflectionHelper.applySetterFromExistingFields(newInfo, cliente, dtoFullClassName, entityFullClassName);
			clienteRepository.save(cliente);
			return cliente;

		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			throw e;
		}
	}

	public Proveedor updateProveedor(Long proovedorId, ProveedorInfoUpdateDTO newInfo)
			throws UserNotFoundException, ImageNotUploadedException, ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Optional<Proveedor> proveedorOpt = this.proveedorRepository.findById(proovedorId);

		if (!proveedorOpt.isPresent()) {
			throw new UserNotFoundException("El usuario no existe");
		}

		Proveedor proveedor = proveedorOpt.get();

		if (Optional.ofNullable(newInfo.getFotoPerfilUrl()).isPresent()) {
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(microservicioImagenesUrl + ImagenesControllerUrls.IMAGE_BASE_URL)
					.queryParam("imagePath", newInfo.getFotoPerfilUrl());

			ResponseEntity imageExistsResponse = httpClient.getForEntity(uriBuilder.toUriString(), Void.class);

			if (imageExistsResponse.getStatusCodeValue() != 200) {
				throw new ImageNotUploadedException();
			}
		}

		String dtoFullClassName = ReflectionHelper.getObjectClassFullName(newInfo);

		String entityFullClassName = ReflectionHelper.getObjectClassFullName(proveedor);

		ReflectionHelper.applySetterFromExistingFields(newInfo, proveedor, dtoFullClassName, entityFullClassName);

		proveedorRepository.save(proveedor);

		return proveedor;

	}

	public boolean proveedorExistsByIdAndType(Long id, ProveedorType proveedorType) {
		return proveedorRepository.existsByIdAndProveedorType(id, proveedorType);
	}

	public boolean usuarioExists(Long id) throws UserNotFoundException {
		boolean usuarioExists = usuarioRepository.existsById(id);

		if (usuarioExists) {
			return true;
		}
		throw new UserNotFoundException();
	}

	public Usuario findByEmail(String email, boolean checkIfInactive) throws UserNotFoundException, UserInactiveException {

		Usuario usuario = usuarioRepository.findByEmail(email);

		if (usuario == null)
			throw new UserNotFoundException();

		if (checkIfInactive && !usuario.isActive())
			throw new UserInactiveException(getMessageTag("exceptions.account.disabled"));
		
		if (checkIfInactive && !usuario.isAccountVerified())
			throw new UserInactiveException(getMessageTag("exceptions.account.emailNotVerified"));

		return usuario;
	}

	public Usuario findById(Long id, boolean handleLoginExceptions) throws UserNotFoundException {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
		if (usuarioOpt.isPresent()) {
			return usuarioOpt.get();
		}

		if (handleLoginExceptions) {
			throw new UserNotFoundException();
		} else {
			throw new UserNotFoundException("Usuario no encontrado");
		}
	}
	
	public boolean isTwoFactorCodeValid(String jwt) {				
		String url = serviceSecurityUrl + SecurityControllerUrls.CHECK_USER_2FA.replace("{jwt}", jwt);
				
		return httpClient.getForObject(url, Boolean.class);
	}

	public void addVendible(Long vendibleId, Long proveedorId, ProveedorVendible proveedorVendible)
			throws VendibleBindingException, VendibleAlreadyBindedException {

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(microservicioVendibleUrl)
				.path(VendiblesControllersUrls.INTERNAL_GET_VENDIBLE).queryParam("vendibleId", vendibleId);

		String getVendibleUrl = builder.toUriString();

		Optional<Vendible> vendibleOpt = Optional.ofNullable(httpClient.getForObject(getVendibleUrl, Vendible.class));
		Optional<Proveedor> proveedorOpt = proveedorRepository.findById(proveedorId);

		if (vendibleOpt.isPresent() && proveedorOpt.isPresent()) {
			String proveedorType = proveedorOpt.get().getProveedorType().toString();

			String getVendibleTypeUrl = microservicioVendibleUrl
					+ VendiblesControllersUrls.GET_VENDIBLE_TYPE.replace("{vendibleId}", vendibleId.toString());
			Optional<String> vendibleTypeOpt = Optional
					.ofNullable(httpClient.getForObject(getVendibleTypeUrl, String.class));

			String vendibleType = vendibleTypeOpt.get();

			boolean typesMatch = vendibleType.equalsIgnoreCase(VendibleType.PRODUCTO.toString())
					&& proveedorType.equalsIgnoreCase(ProveedorType.PRODUCTOS.toString())
					|| vendibleType.equalsIgnoreCase(VendibleType.SERVICIO.toString())
							&& proveedorType.equalsIgnoreCase(ProveedorType.SERVICIOS.toString());

			if (typesMatch) {
				try {
					VendibleAccesor toBindVendible = vendibleOpt.get();
					toBindVendible.setId(vendibleId);
					proveedorVendibleService.bindVendibleToProveedor(toBindVendible, proveedorOpt.get(),
							proveedorVendible);
				} catch (DataIntegrityViolationException e) {
					throw new VendibleAlreadyBindedException();
				}
			} else {
				throw new VendibleBindingException();
			}
		} else {
			throw new VendibleBindingException();
		}
	}

	public Object translateCoordinates(double latitude, double longitude) {
		String baseUrl = openStreetAPIUrl + "reverse?lat=" + latitude + "&lon=" + longitude + "&format=json";
		try {
			return httpClient.getForObject(baseUrl, Object.class);
		} catch (Exception e) {
			CustomException castedException = (CustomException) e;
			return new ExceptionFactory().getResponseException(castedException.getMessage(),
					HttpStatusCode.valueOf(castedException.getStatusCode()));
		}
	}

	public Object getUsuarioField(String field, Long userId) throws UserNotFoundException, IllegalAccessException {
		Usuario user = this.findById(userId, false);
		Map<String, Object> fields = ReflectionHelper.getObjectFields(user);
		if (!fields.containsKey(field)) {
			return null;
		}

		return fields.get(field);
	}

	@Transactional
	public void sendRegistrationLinkEmail(String email)
			throws UserNotFoundException, UserInactiveException, AccountVerificationException {
		Usuario foundUser = this.findByEmail(email, false);
		String linkToken;

		if (foundUser.isAccountVerified()) {
			throw new AccountVerificationException(this.getMessageTag("exceptions.account.alreadyVerified"));
		}

		Optional<String> storedTokenOpt = Optional.ofNullable(foundUser.getAccountVerificationToken());

		if (storedTokenOpt.isEmpty() || !StringUtils.hasLength(storedTokenOpt.get())) {
			linkToken = getNewUserToken(email, foundUser);
		} else {
			String storedToken = storedTokenOpt.get();

			boolean isTokenOk = checkUserToken(storedToken);

			if (isTokenOk) {
				throw new AccountVerificationException(getMessageTag("exceptions.account.linkAlreayRequested"));
			} else {
				linkToken = getNewUserToken(email, foundUser);
			}

		}

		ResponseEntity response = httpClient.postForEntity(
				mailingServiceUrl + UsersControllerUrls.SEND_REGISTRATION_LINK_EMAIL,
				new RegistrationLinkMailInfo(email, linkToken), Void.class);

		if (response.getStatusCodeValue() != 200) {
			throw new AccountVerificationException(getMessageTag("exceptions.account.couldntSendEmail"));
		}
	}

	@Transactional
	public void acceptUserAccountActivation(String email, String token)
			throws UserNotFoundException, UserInactiveException, AccountVerificationException {
		Usuario foundUser = this.findByEmail(email, false);

		if (foundUser.isAccountVerified()) {
			throw new AccountVerificationException(this.getMessageTag("exceptions.account.alreadyVerified"), 401);
		}

		Optional<String> storedTokenOpt = Optional.ofNullable(foundUser.getAccountVerificationToken());

		boolean isTokenEmpty = storedTokenOpt.isEmpty() || !StringUtils.hasLength(storedTokenOpt.get());

		if (isTokenEmpty || !token.equals(storedTokenOpt.get()) || !checkUserToken(storedTokenOpt.get())) {
			throw new AccountVerificationException(getMessageTag("exceptions.account.wrongToken"), 400);
		}

		foundUser.setAccountVerified(true);
		foundUser.setAccountVerificationToken("");
		usuarioRepository.save(foundUser);

		httpClient.postForEntity(mailingServiceUrl + UsersControllerUrls.SIGNUP_OK_EMAIL, new MailInfo(email),
				Void.class);

	}
}
