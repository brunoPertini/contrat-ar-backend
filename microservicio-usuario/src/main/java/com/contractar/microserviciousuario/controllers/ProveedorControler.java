package com.contractar.microserviciousuario.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.constants.controllers.ProveedorControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.SuscriptionActiveUpdateDTO;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleFilter;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.SimplifiedVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleProveedoresDTO;
import com.contractar.microserviciocommons.exceptions.CantCreateSuscription;
import com.contractar.microserviciocommons.exceptions.CantUpdateUserException;
import com.contractar.microserviciocommons.exceptions.ImageNotUploadedException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciousuario.admin.dtos.PostsResponseDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorPersonalDataUpdateDTO;
import com.contractar.microserviciousuario.admin.services.ChangeConfirmException;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;
import com.contractar.microserviciousuario.models.Suscripcion;
import com.contractar.microserviciousuario.services.ProveedorService;
import com.contractar.microserviciousuario.services.ProveedorVendibleService;
import com.contractar.microserviciousuario.services.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ProveedorControler {
	@Autowired
	private ProveedorVendibleService proveedorVendibleService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ProveedorService proveedorService;

	private static final int DEFAULT_PAGE_SIZE = 10;

	@GetMapping(ProveedorControllerUrls.INTERNAL_PLAN_BASE_URL)
	public ResponseEntity<?> getAllPlans() {
		return new ResponseEntity<>(proveedorService.findAll(), HttpStatus.OK);
	}
	
	@GetMapping(ProveedorControllerUrls.GET_SUSCRIPCION)
	public ResponseEntity<?> getSuscripcionById(@PathVariable Long suscriptionId,
			@RequestParam(defaultValue = "false") String getAsEntity) throws SuscriptionNotFound {
		
		// TODO: add access control to ensure this endpoint can only be accesed by admin and by the proveedor that matches with subscription
		Suscripcion suscripcion = this.proveedorService.findSuscripcionById(suscriptionId);
		
		boolean getAsEntityBool = Boolean.parseBoolean(getAsEntity);
		
		Long userId = Optional.ofNullable(suscripcion.getUsuario()).map(u -> u.getId()).orElse(null);
		
		return  new ResponseEntity<>(!getAsEntityBool ? new SuscripcionDTO(suscripcion.getId(), suscripcion.isActive(),
				userId,
				suscripcion.getPlan().getId(),
				suscripcion.getCreatedDate(),
				suscripcion.getPlan().getPrice()) : suscripcion, HttpStatus.OK);
	}

	@GetMapping(ProveedorControllerUrls.GET_PROVEEDOR_SUSCRIPCION)
	public ResponseEntity<SuscripcionDTO> getSuscripcion(@PathVariable Long proveedorId) throws UserNotFoundException {
		Suscripcion suscripcion = (Suscripcion) proveedorService.findById(proveedorId).getSuscripcion();

		return  new ResponseEntity<>(new SuscripcionDTO(suscripcion.getId(), suscripcion.isActive(),
				suscripcion.getUsuario().getId(),
				suscripcion.getPlan().getId(), suscripcion.getCreatedDate(),
				suscripcion.getPlan().getPrice()), HttpStatus.OK);
	}
	
	@PutMapping(ProveedorControllerUrls.GET_PROVEEDOR_SUSCRIPCION)
	public ResponseEntity<?> updateLinkedSuscripcion(@PathVariable Long proveedorId, @RequestBody SuscriptionActiveUpdateDTO body) {
		this.proveedorService.updateLinkedSubscription(proveedorId, body);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping(ProveedorControllerUrls.POST_PROVEEDOR_SUSCRIPCION)
	public ResponseEntity<SuscripcionDTO> createSuscripcion(@PathVariable Long proveedorId, @PathVariable Long planId) throws UserNotFoundException,
	CantCreateSuscription {
		return new ResponseEntity<SuscripcionDTO>(proveedorService.createSuscripcion(proveedorId, planId), HttpStatus.CREATED);
	}

	@Deprecated(forRemoval = true)
	@GetMapping(ProveedorControllerUrls.GET_VENDIBLES_OF_PROVEEDOR)
	public ResponseEntity<ProveedorVendiblesResponseDTO> getVendiblesInfoOfProveedor(
			@PathVariable("proveedorId") Long proveedorId) {
		return new ResponseEntity<ProveedorVendiblesResponseDTO>(
				proveedorVendibleService.getProveedorVendiblesInfo(proveedorId, null), HttpStatus.OK);
	}
	
	@PostMapping(ProveedorControllerUrls.GET_VENDIBLES_OF_PROVEEDOR)
	public ResponseEntity<ProveedorVendiblesResponseDTO> getVendiblesInfoOfProveedorV2(@PathVariable("proveedorId") Long proveedorId,
			@RequestBody(required = false) ProveedorVendibleFilter filters) {
		return new ResponseEntity<ProveedorVendiblesResponseDTO>(
				proveedorVendibleService.getProveedorVendiblesInfo(proveedorId, filters), HttpStatus.OK);
	}

	@GetMapping(VendiblesControllersUrls.GET_VENDIBLE_POSTS)
	public ResponseEntity<VendibleProveedoresDTO> getProveedoresOfVendible(@PathVariable("vendibleId") Long vendibleId,
			@RequestParam(name = "filter_distance_min", required = false) Double minDistance,
			@RequestParam(name = "filter_distance_max", required = false) Double maxDistance,
			@RequestParam(name = "filter_price_min", required = false) Integer minPrice,
			@RequestParam(name = "filter_price_max", required = false) Integer maxPrice, @RequestParam int page,
			HttpServletRequest request) throws JsonProcessingException {

		return new ResponseEntity<>(proveedorVendibleService.getProveedoreVendiblesInfoForVendible(vendibleId,
				minDistance, maxDistance, minPrice, maxPrice, request, PageRequest.of(page, DEFAULT_PAGE_SIZE)),
				HttpStatus.OK);

	}

	@PutMapping("/proveedor/{proveedorId}")
	public ResponseEntity<?> updateProveedorInfo(@PathVariable Long proveedorId,
			@RequestBody @Valid ProveedorPersonalDataUpdateDTO body, HttpServletRequest request) throws UserNotFoundException, ImageNotUploadedException,
			ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ChangeConfirmException, CantUpdateUserException {
		String parsedJwt = request.getHeader("Authorization").replace("Bearer ", "");
		Proveedor updated = usuarioService.updateProveedor(proveedorId, body, parsedJwt);

		return new ResponseEntity<>(new ProveedorDTO(updated), HttpStatus.CREATED);
	}

	@PostMapping(VendiblesControllersUrls.GET_VENDIBLE_POSTS_V2)
	public ResponseEntity<PostsResponseDTO> getPostsOfVendible(@PathVariable("vendibleId") Long vendibleId,
			@RequestParam int page, @RequestBody(required = false) ProveedorVendibleFilter filters) {
		return new ResponseEntity<>(
				proveedorVendibleService.getPostsOfVendible(vendibleId, page, DEFAULT_PAGE_SIZE, filters),
				HttpStatus.OK);
	}
	
	@GetMapping(VendiblesControllersUrls.GET_VENDIBLE_DETAIL)
	public ResponseEntity<SimplifiedVendibleDTO> seePostDetail(@PathVariable("vendibleId") Long vendibleId,
			@PathVariable("proveedorId") Long proveedorId) throws VendibleNotFoundException {
		ProveedorVendible entity = proveedorVendibleService.findById(new ProveedorVendibleId(proveedorId, vendibleId));
		SimplifiedVendibleDTO dto = new SimplifiedVendibleDTO(entity, List.of());
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
}
