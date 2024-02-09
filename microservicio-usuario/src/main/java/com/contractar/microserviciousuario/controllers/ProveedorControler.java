package com.contractar.microserviciousuario.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleProveedoresDTO;
import com.contractar.microserviciocommons.infra.SecurityHelper;
import com.contractar.microserviciooauth.helpers.JwtHelper;
import com.contractar.microserviciousuario.services.ProveedorVendibleService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ProveedorControler {
	@Autowired
	private ProveedorVendibleService proveedorVendibleService;
	
	@Autowired
	private SecurityHelper securityHelper;

	@GetMapping("/proveedor/{proveedorId}/vendible")
	public ResponseEntity<ProveedorVendiblesResponseDTO> getVendiblesInfoOfProveedor(@PathVariable("proveedorId") Long proveedorId) {
		return new ResponseEntity<ProveedorVendiblesResponseDTO>(proveedorVendibleService.getProveedorVendiblesInfo(proveedorId),
				HttpStatus.OK);
	}
	
	@GetMapping("/vendible/{vendibleId}/proveedores")
	public ResponseEntity<VendibleProveedoresDTO> getProveedoresOfVendible(HttpServletRequest request, @PathVariable("vendibleId") Long vendibleId) throws JsonProcessingException {
		Long clienteId = (Long) securityHelper.getValueFromJwt("id", request.getHeader("Authorization"));
		return new ResponseEntity<>(proveedorVendibleService.getProveedoreVendiblesInfoForVendible(vendibleId, clienteId), HttpStatus.OK);
	}
}
