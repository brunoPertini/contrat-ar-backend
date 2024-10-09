package com.contractar.microserviciousuario.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.constants.controllers.GeoControllersUrls;
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleUpdateDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioSensibleInfoDTO;
import com.contractar.microserviciocommons.exceptions.AccountVerificationException;
import com.contractar.microserviciocommons.exceptions.UserCreationException;
import com.contractar.microserviciocommons.exceptions.UserInactiveException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleBindingException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleUpdateException;
import com.contractar.microserviciocommons.infra.ExceptionFactory;
import com.contractar.microserviciocommons.mailing.RegistrationLinkMailInfo;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciousuario.admin.services.AdminService;
import com.contractar.microserviciousuario.admin.services.ChangeAlreadyRequestedException;
import com.contractar.microserviciousuario.dtos.UsuarioOauthDTO;
import com.contractar.microserviciousuario.helpers.DtoHelper;
import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.services.ProveedorVendibleService;
import com.contractar.microserviciousuario.services.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class UsuarioController {
	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ProveedorVendibleService proveedorVendibleService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private DtoHelper dtoHelper;

	@PostMapping("/usuarios")
	public ResponseEntity<Usuario> crearUsuario(@RequestBody @Valid Usuario usuario) {
		Usuario createdUsuario = usuarioService.create(usuario);
		return new ResponseEntity<Usuario>(createdUsuario, HttpStatus.CREATED);
	}

	@PostMapping(UsersControllerUrls.CREATE_PROVEEDOR)
	public ResponseEntity<?> crearProveedor(@RequestBody @Valid Proveedor usuario) throws UserCreationException {
		try {
			Proveedor createdUsuario = usuarioService.createProveedor(usuario);
			
			String createdUserToken = usuarioService.getTokenForCreatedUser(createdUsuario.getEmail(), createdUsuario.getId());
			ProveedorDTO responseBody = DtoHelper.toProveedorDTO(createdUsuario);
			responseBody.setCreationToken(createdUserToken);		

			return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
		} catch (Exception e) {
			throw new UserCreationException();
		}
	}

	@PostMapping(UsersControllerUrls.CREATE_CLIENTE)
	public ResponseEntity<UsuarioDTO> crearCliente(@RequestBody @Valid Cliente usuario) throws UserCreationException {
		try {
			Cliente createdUsuario = usuarioService.createCliente(usuario);
			
			String createdUserToken = usuarioService.getTokenForCreatedUser(createdUsuario.getEmail(), createdUsuario.getId());
			UsuarioDTO responseBody = DtoHelper.toUsuarioDTO(createdUsuario);
			responseBody.setCreationToken(createdUserToken);		
			
			return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
		} catch (Exception e) {
			throw new UserCreationException();
		}

	}

	@SuppressWarnings("rawtypes")
	@GetMapping(UsersControllerUrls.USUARIO_BASE_URL)
	public ResponseEntity usuarioExists(@PathVariable Long usuarioId) throws UserNotFoundException {
		boolean usuarioExists = usuarioService.usuarioExists(usuarioId);
		int responseStatus = usuarioExists ? 200 : 404;
		return new ResponseEntity(HttpStatusCode.valueOf(responseStatus));
	}

	@GetMapping(UsersControllerUrls.GET_USUARIOS)
	public ResponseEntity<UsuarioOauthDTO> findByParam(@RequestParam(required = false) String email,
			@RequestParam(required = false) Long id,
			@RequestParam(required = false, defaultValue = "true") String checkIfInactive) throws UserNotFoundException, UserInactiveException {
		Usuario usuario = email != null ? usuarioService.findByEmail(email, Boolean.getBoolean(checkIfInactive)) : usuarioService.findById(id, true);

		UsuarioOauthDTO usuarioOauthDTO = new UsuarioOauthDTO(usuario.getId(), usuario.getName(), usuario.getSurname(),
				usuario.getEmail(), usuario.isActive(), usuario.getPassword(), new ArrayList<SimpleGrantedAuthority>(),
				usuario.getRole());
		return new ResponseEntity<UsuarioOauthDTO>(usuarioOauthDTO, HttpStatus.OK);
	}

	@GetMapping(UsersControllerUrls.GET_USUARIO_INFO)
	public ResponseEntity<? extends UsuarioDTO> findUserInfo(@PathVariable("userId") Long userId,
			@RequestParam(name = "formatType", required = false) DateFormatType formatType)
			throws UserNotFoundException {
		Usuario user = this.usuarioService.findById(userId, false);
		if (user.getRole().getNombre().startsWith("PROVEEDOR_")) {
			Proveedor proveedor = ((Proveedor) user);
			ProveedorDTO proveedorDTO = dtoHelper.toProveedorDTO(proveedor, formatType);
			proveedorDTO.setRole(proveedor.getRole());
			return new ResponseEntity<>(proveedorDTO, HttpStatus.OK);
		}
		;

		UsuarioDTO usuarioDTO = DtoHelper.toUsuarioDTO(user);
		usuarioDTO.setRole(user.getRole());

		return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
	}

	@GetMapping(UsersControllerUrls.GET_USUARIO_FIELD)
	public ResponseEntity<Object> getUsuarioFields(@PathVariable("userId") Long userId,
			@PathVariable("fieldName") String field) throws UserNotFoundException, IllegalAccessException {
		Object fieldValue = usuarioService.getUsuarioField(field, userId);
		return fieldValue != null ? new ResponseEntity<>(fieldValue, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);

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

	@DeleteMapping(UsersControllerUrls.PROVEEDOR_VENDIBLE)
	public ResponseEntity<Void> unBindVendible(@PathVariable Long vendibleId, @PathVariable Long proveedorId)
			throws VendibleNotFoundException {
		proveedorVendibleService.unBindVendible(vendibleId, proveedorId);
		return new ResponseEntity<Void>(HttpStatusCode.valueOf(204));
	}

	@PutMapping(UsersControllerUrls.PROVEEDOR_VENDIBLE)
	public ResponseEntity<?> updateVendible(@PathVariable Long vendibleId, @PathVariable Long proveedorId,
			@Valid @RequestBody ProveedorVendibleUpdateDTO body, HttpServletRequest request)
			throws VendibleNotFoundException, VendibleUpdateException, InvocationTargetException,
			IllegalAccessException, ClassNotFoundException {

		proveedorVendibleService.updateVendible(vendibleId, proveedorId, body, request);
		return new ResponseEntity<Void>(HttpStatusCode.valueOf(200));
	}

	@PutMapping(UsersControllerUrls.USUARIO_BASE_URL)
	public ResponseEntity<?> changeUserSensibleInfo(@PathVariable Long usuarioId,
			@RequestBody UsuarioSensibleInfoDTO body) throws ChangeAlreadyRequestedException {
		try {
			adminService.addChangeRequestEntry(body, List.of(usuarioId.toString()));
			return ResponseEntity.ok().build();
		} catch (IllegalAccessException e) {
			return new ExceptionFactory().getResponseException(
					"Hay algún error con los campos que estas tratando de actualizar", HttpStatusCode.valueOf(409));
		}
	}

	@GetMapping(GeoControllersUrls.TRANSLATE_COORDINATES)
	public ResponseEntity<?> translateAddress(@RequestParam("latitude") double latitude,
			@RequestParam("longitude") double longitude) {
		return new ResponseEntity<>(this.usuarioService.translateCoordinates(latitude, longitude), HttpStatus.OK);
	}

	@PostMapping(UsersControllerUrls.SEND_REGISTRATION_LINK_EMAIL)
	public ResponseEntity<?> sendRegistrationLinkEmail(@RequestParam(required = true) String email)
			throws UserNotFoundException, UserInactiveException, AccountVerificationException {
		this.usuarioService.sendRegistrationLinkEmail(email);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping(UsersControllerUrls.SIGNUP_OK_EMAIL)
	public ResponseEntity<?> confirmUserAccount(@RequestBody RegistrationLinkMailInfo body)
			throws UserNotFoundException, UserInactiveException, AccountVerificationException {
		this.usuarioService.acceptUserAccountActivation(body.getToAddress(), body.getToken());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
