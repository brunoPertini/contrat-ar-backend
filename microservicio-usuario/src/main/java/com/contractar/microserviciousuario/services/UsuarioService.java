package com.contractar.microserviciousuario.services;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.usuario.ProveedorInfoUpdateDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioCommonInfoUpdateDTO;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioAbstractDTO;
import com.contractar.microserviciocommons.exceptions.CustomException;
import com.contractar.microserviciocommons.exceptions.ImageNotUploadedException;
import com.contractar.microserviciocommons.exceptions.UserCreationException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleBindingException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciocommons.vendibles.VendibleType;

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

	@Value("${microservicio-vendible.url}")
	private String microservicioVendibleUrl;

	@Value("${openstreet-api.url}")
	private String openStreetAPIUrl;
	
	@Value("${microservicio-imagenes.url}")
	private String microservicioImagenesUrl;
	
	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;
	
	private void requestUsuarioActiveFlag(Long userId) throws UserCreationException {
		String url = microservicioUsuarioUrl + AdminControllerUrls.ADMIN_USUARIOS_BY_ID.replace("{id}", userId.toString());
		try {
			httpClient.put(url, new UsuarioAbstractDTO(true));
		} catch (RestClientException e) {
			throw new UserCreationException();
		}
	}

	public Usuario create(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	@Transactional(rollbackOn = {UserCreationException.class})
	public Proveedor createProveedor(Proveedor proveedor) throws UserCreationException {
		String roleName = "PROVEEDOR_" + proveedor.getProveedorType().toString();
		Optional<Role> roleOpt = roleRepository.findByNombre(roleName);
		if (roleOpt.isPresent()) {
			proveedor.setRole(roleOpt.get());
			proveedor.setCreatedAt(LocalDate.now());
			Proveedor newProveedor = proveedorRepository.save(proveedor);
			requestUsuarioActiveFlag(newProveedor.getId());
			return newProveedor;
		}
		throw new UserCreationException();

	}

	@Transactional(rollbackOn = {UserCreationException.class})
	public Cliente createCliente(Cliente cliente) throws UserCreationException {
		Role clienteRole = roleRepository.findByNombre(RolesValues.CLIENTE.toString()).get();
		cliente.setRole(clienteRole);
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
			throws UserNotFoundException, ImageNotUploadedException, 
			ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Optional<Proveedor> proveedorOpt = this.proveedorRepository.findById(proovedorId);

		if (!proveedorOpt.isPresent()) {
			throw new UserNotFoundException("El usuario no existe");
		}
		
		Proveedor proveedor = proveedorOpt.get();

		if (Optional.ofNullable(newInfo.getFotoPerfilUrl()).isPresent()) {
			 UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(microservicioImagenesUrl + ImagenesControllerUrls.IMAGE_BASE_URL)
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

	public Usuario findByEmail(String email) throws UserNotFoundException {

		Usuario usuario = usuarioRepository.findByEmail(email);

		if (usuario != null) {
			return usuario;
		}
		throw new UserNotFoundException();
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

	public void addVendible(Long vendibleId, Long proveedorId, ProveedorVendible proveedorVendible)
			throws VendibleBindingException, VendibleAlreadyBindedException {

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(microservicioVendibleUrl)
				.path(VendiblesControllersUrls.GET_VENDIBLE).queryParam("vendibleId", vendibleId);

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
}
