package com.contractar.microserviciousuario.services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.Role;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.repository.ClienteRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.RoleRepository;
import com.contractar.microserviciousuario.repository.UsuarioRepository;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciocommons.constants.RolesNames;
import com.contractar.microserviciocommons.constants.RolesNames.RolesValues;
import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.VendibleBindingException;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
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
	
	public Usuario create(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Proveedor createProveedor(Proveedor proveedor) throws Exception {
		String roleName = "PROVEEDOR_" + proveedor.getProveedorType().toString();
		Optional<Role> roleOpt = roleRepository.findByNombre(roleName);
		if (roleOpt.isPresent()) {
			proveedor.setRole(roleOpt.get());
			return proveedorRepository.save(proveedor);
		}
		throw new Exception("Rol de usuario invalido");
		
	}

	public boolean proveedorExistsByIdAndType(Long id, ProveedorType proveedorType) {
		return proveedorRepository.existsByIdAndProveedorType(id, proveedorType);
	}

	public Cliente createCliente(Cliente cliente) {
		Role clienteRole = roleRepository.findByNombre(RolesValues.CLIENTE.toString()).get();
		cliente.setRole(clienteRole);
		return clienteRepository.save(cliente);
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

	public Usuario findById(Long id) throws UserNotFoundException {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
		if (usuarioOpt.isPresent()) {
			return usuarioOpt.get();
		}
		throw new UserNotFoundException();
	}

	public void addVendible(Long vendibleId, Long proveedorId, ProveedorVendible proveedorVendible)
			throws VendibleBindingException, VendibleAlreadyBindedException {
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(microservicioVendibleUrl)
                .path(VendiblesControllersUrls.GET_VENDIBLE)
                .queryParam("vendibleId", vendibleId);
		
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
					proveedorVendibleService.bindVendibleToProveedor(vendibleOpt.get(),
							proveedorOpt.get(),
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
}
