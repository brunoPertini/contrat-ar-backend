package com.contractar.microserviciousuario.controllers;

import java.util.ArrayList;

import org.locationtech.jts.geom.Point;
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
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleUpdateDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioSensibleInfoDTO;
import com.contractar.microserviciocommons.exceptions.UserCreationException;
import com.contractar.microserviciocommons.exceptions.UserInactiveException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleBindingException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleUpdateException;
import com.contractar.microserviciocommons.helpers.DistanceCalculator;
import com.contractar.microserviciocommons.infra.ExceptionFactory;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.Valid;

@RestController
public class UsuarioController {
	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ProveedorVendibleService proveedorVendibleService;

	@Autowired
	private AdminService adminService;
	
	private static class PointInRadiusBody {
		
		@JsonDeserialize(using = UbicacionDeserializer.class)
		@JsonSerialize(using = UbicacionSerializer.class)
		private Point source;
		
		private double radiusKm;
		
		@JsonDeserialize(using = UbicacionDeserializer.class)
		@JsonSerialize(using = UbicacionSerializer.class)
		private Point toComparePoint;
		
		public PointInRadiusBody() {}
		
		public Point getSource() {
			return source;
		}
		public void setSource(Point source) {
			this.source = source;
		}
		public double getRadiusKm() {
			return radiusKm;
		}
		public void setRadiusKm(double radiusKm) {
			this.radiusKm = radiusKm;
		}
		public Point getToComparePoint() {
			return toComparePoint;
		}
		public void setToComparePoint(Point toComparePoint) {
			this.toComparePoint = toComparePoint;
		}
	}

	@PostMapping("/usuarios")
	public ResponseEntity<Usuario> crearUsuario(@RequestBody @Valid Usuario usuario) {
		Usuario createdUsuario = usuarioService.create(usuario);
		return new ResponseEntity<Usuario>(createdUsuario, HttpStatus.CREATED);
	}

	@PostMapping(UsersControllerUrls.CREATE_PROVEEDOR)
	public ResponseEntity<?> crearProveedor(@RequestBody @Valid Proveedor usuario) throws UserCreationException {
		try {
			Proveedor createdUsuario = usuarioService.createProveedor(usuario);
			return new ResponseEntity<>(DtoHelper.toProveedorDTO(createdUsuario), HttpStatus.CREATED);
		} catch (Exception e) {
			throw new UserCreationException();
		}
	}

	@PostMapping(UsersControllerUrls.CREATE_CLIENTE)
	public ResponseEntity<UsuarioDTO> crearCliente(@RequestBody @Valid Cliente usuario) throws UserCreationException {
		try {
			Cliente createdUsuario = usuarioService.createCliente(usuario);
			return new ResponseEntity<>(DtoHelper.toUsuarioDTO(createdUsuario), HttpStatus.CREATED);
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
			@RequestParam(required = false) Long id) throws UserNotFoundException, UserInactiveException {
		Usuario usuario = email != null ? usuarioService.findByEmail(email) : usuarioService.findById(id, true);

		UsuarioOauthDTO usuarioOauthDTO = new UsuarioOauthDTO(usuario.getId(), usuario.getName(), usuario.getSurname(),
				usuario.getEmail(), usuario.isActive(), usuario.getPassword(), new ArrayList<SimpleGrantedAuthority>(),
				usuario.getRole());
		return new ResponseEntity<UsuarioOauthDTO>(usuarioOauthDTO, HttpStatus.OK);
	}

	@GetMapping(UsersControllerUrls.GET_USUARIO_INFO)
	public ResponseEntity<? extends UsuarioDTO> findUserInfo(@PathVariable("userId") Long userId)
			throws UserNotFoundException {
		Usuario user = this.usuarioService.findById(userId, false);
		if (user.getRole().getNombre().startsWith("PROVEEDOR_")) {
			Proveedor proveedor = ((Proveedor) user);
			ProveedorDTO proveedorDTO = DtoHelper.toProveedorDTO(proveedor);
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
		return fieldValue != null ? new ResponseEntity<>(fieldValue, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
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
	public ResponseEntity<Void> updateVendible(@PathVariable Long vendibleId, @PathVariable Long proveedorId,
			@Valid @RequestBody ProveedorVendibleUpdateDTO body)
			throws VendibleNotFoundException, VendibleUpdateException {
		proveedorVendibleService.updateVendible(vendibleId, proveedorId, body);
		return new ResponseEntity<Void>(HttpStatusCode.valueOf(200));
	}

	@PutMapping(UsersControllerUrls.USUARIO_BASE_URL)
	public ResponseEntity<?> changeUserSensibleInfo(@PathVariable Long usuarioId,
			@RequestBody UsuarioSensibleInfoDTO body) throws ChangeAlreadyRequestedException {
		try {
			adminService.addChangeRequestEntry(body, usuarioId);
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
	
	@PostMapping("/geo/radius/check")
	public ResponseEntity<?> checkPointInPointRadius(@RequestBody PointInRadiusBody body) {
		boolean result = DistanceCalculator.calculateDistance(body.getSource(), body.getToComparePoint()) <= body.getRadiusKm();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
