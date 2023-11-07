package com.contractar.microserviciousuario.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.dto.vendibles.SimplifiedVendibleDTO;
import com.contractar.microserviciousuario.services.ProveedorVendibleService;

@RestController
public class ProveedorControler {
	@Autowired
	private ProveedorVendibleService proveedorVendibleService;

	@GetMapping("/proveedor/{proveedorId}/vendible")
	public ResponseEntity<List<SimplifiedVendibleDTO>> getVendiblesInfoOfProveedor(@PathVariable("proveedorId") Long proveedorId) {
		return new ResponseEntity<List<SimplifiedVendibleDTO>>(proveedorVendibleService.getProveedorVendiblesInfo(proveedorId),
				HttpStatus.OK);
	}
}
