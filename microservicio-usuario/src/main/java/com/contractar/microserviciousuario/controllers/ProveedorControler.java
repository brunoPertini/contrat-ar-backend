package com.contractar.microserviciousuario.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleProveedoresDTO;
import com.contractar.microserviciousuario.services.ProveedorVendibleService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ProveedorControler {
	@Autowired
	private ProveedorVendibleService proveedorVendibleService;

	@GetMapping("/proveedor/{proveedorId}/vendible")
	public ResponseEntity<ProveedorVendiblesResponseDTO> getVendiblesInfoOfProveedor(
			@PathVariable("proveedorId") Long proveedorId) {
		return new ResponseEntity<ProveedorVendiblesResponseDTO>(
				proveedorVendibleService.getProveedorVendiblesInfo(proveedorId), HttpStatus.OK);
	}

	@GetMapping("/vendible/{vendibleId}/proveedores")
	public ResponseEntity<VendibleProveedoresDTO> getProveedoresOfVendible(@PathVariable("vendibleId") Long vendibleId,
			@RequestParam(name = "filter_distance_min", required = false) Double minDistance,
			@RequestParam(name = "filter_distance_max", required = false) Double maxDistance,
			@RequestParam(name = "filter_price_min", required = false) Integer minPrice,
			@RequestParam(name = "filter_price_max", required = false) Integer maxPrice,
			HttpServletRequest request) throws JsonProcessingException {
		return new ResponseEntity<>(proveedorVendibleService.getProveedoreVendiblesInfoForVendible(vendibleId,
				minDistance, maxDistance, minPrice, maxPrice, request), HttpStatus.OK);
	}
}
