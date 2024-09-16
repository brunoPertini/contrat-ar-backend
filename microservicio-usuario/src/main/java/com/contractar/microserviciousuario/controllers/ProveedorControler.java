package com.contractar.microserviciousuario.controllers;

import java.lang.reflect.InvocationTargetException;

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
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleFilter;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorInfoUpdateDTO;
import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleProveedoresDTO;
import com.contractar.microserviciocommons.exceptions.ImageNotUploadedException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciousuario.admin.dtos.PostsResponseDTO;
import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.models.Proveedor;
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

	@GetMapping(ProveedorControllerUrls.GET_PLAN_BY_ID)
	public ResponseEntity<?> getPlan(@PathVariable("planId") Long planId) {
		Plan foundPlan = proveedorService.findPlanById(planId);

		return foundPlan != null ? new ResponseEntity<>(foundPlan, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
	public ResponseEntity<?> updateProveedorInfo(@PathVariable("proveedorId") Long proveedorId,
			@RequestBody @Valid ProveedorInfoUpdateDTO body) throws UserNotFoundException, ImageNotUploadedException,
			ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Proveedor updated = usuarioService.updateProveedor(proveedorId, body);

		return new ResponseEntity<>(new ProveedorDTO(updated), HttpStatus.CREATED);
	}

	@PostMapping(VendiblesControllersUrls.GET_VENDIBLE_POSTS_V2)
	public ResponseEntity<PostsResponseDTO> getPostsOfVendible(@PathVariable("vendibleId") Long vendibleId,
			@RequestParam int page, @RequestBody(required = false) ProveedorVendibleFilter filters) {
		return new ResponseEntity<>(
				proveedorVendibleService.getPostsOfVendible(vendibleId, page, DEFAULT_PAGE_SIZE, filters),
				HttpStatus.OK);
	}
}
