package com.contractar.microserviciousuario.controllers;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.dto.UsuarioOauthDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.VendibleBindingException;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.services.UsuarioService;
import jakarta.validation.Valid;

@RestController
public class UsuarioController {
	@Autowired
	private UsuarioService usuarioService;

	@PostMapping("/usuarios")
	public ResponseEntity<Usuario> crearUsuario(@RequestBody @Valid Usuario usuario) {
		Usuario createdUsuario = usuarioService.create(usuario);
		return new ResponseEntity<Usuario>(createdUsuario, HttpStatus.CREATED);
	}

	@PostMapping(UsersControllerUrls.CREATE_PROVEEDOR)
	public ResponseEntity<Proveedor> crearProveedor(@RequestBody @Valid Proveedor usuario) throws Exception {
		Proveedor createdUsuario = usuarioService.createProveedor(usuario);
		return new ResponseEntity<Proveedor>(createdUsuario, HttpStatus.CREATED);
	}

	@PostMapping(UsersControllerUrls.CREATE_CLIENTE)
	public ResponseEntity<Cliente> crearCliente(@RequestBody @Valid Cliente usuario) {
		Cliente createdUsuario = usuarioService.createCliente(usuario);
		return new ResponseEntity<Cliente>(createdUsuario, HttpStatus.CREATED);
	}

	@SuppressWarnings("rawtypes")
	@GetMapping(UsersControllerUrls.USUARIO_EXISTS)
	public ResponseEntity usuarioExists(@PathVariable Long usuarioId) throws UserNotFoundException {
		boolean usuarioExists = usuarioService.usuarioExists(usuarioId);
		int responseStatus = usuarioExists ? 200 : 404;
		return new ResponseEntity(HttpStatusCode.valueOf(responseStatus));
	}

	@GetMapping(UsersControllerUrls.GET_USUARIOS)
	public ResponseEntity<UsuarioOauthDTO> findByParam(@RequestParam(required = false) String email,
			@RequestParam(required = false) Long id) throws UserNotFoundException {
		Usuario usuario = email != null ? usuarioService.findByEmail(email) : usuarioService.findById(id);

		UsuarioOauthDTO usuarioOauthDTO = new UsuarioOauthDTO(usuario.getname(), usuario.getsurname(),
				usuario.getEmail(), usuario.isActive(), usuario.getlocation(), usuario.getPassword(),
				new ArrayList<SimpleGrantedAuthority>(), usuario.getRole());
		return new ResponseEntity<UsuarioOauthDTO>(usuarioOauthDTO, HttpStatus.OK);
	}

	@SuppressWarnings("rawtypes")
	@GetMapping(UsersControllerUrls.GET_PROVEEDOR)
	public ResponseEntity proveedorExists(@RequestParam(required = true) Long id,
			@RequestParam(required = false) ProveedorType proveedorType) {
		boolean proveedorExists = usuarioService.proveedorExistsByIdAndType(id, proveedorType);
		int responseStatus = proveedorExists ? 200 : 404;
		return new ResponseEntity(HttpStatusCode.valueOf(responseStatus));
	}

	@PostMapping(UsersControllerUrls.PROVEEDOR_VENDIBLE)
	public ResponseEntity<Void> addVendible(@PathVariable Long vendibleId, @PathVariable Long proveedorId,
			@RequestBody @Valid ProveedorVendible proveedorVendible)
			throws VendibleBindingException, VendibleAlreadyBindedException {
		usuarioService.addVendible(vendibleId, proveedorId, proveedorVendible);
		return new ResponseEntity<Void>(HttpStatusCode.valueOf(200));
	}
}
