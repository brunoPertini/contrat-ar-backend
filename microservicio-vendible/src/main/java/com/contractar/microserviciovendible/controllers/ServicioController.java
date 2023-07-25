package com.contractar.microserviciovendible.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.services.ServicioService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Controller
public class ServicioController {
	@Autowired
	private ServicioService servicioService;
	
	@PostMapping("/service")
	public ResponseEntity<Servicio> save(@RequestBody @Valid Servicio servicio,
			@RequestParam(required = true) Long proveedorId) {
		return new ResponseEntity<Servicio>(servicioService.save(servicio, proveedorId), HttpStatus.CREATED);
	}
	
	@GetMapping("/service")
	public ResponseEntity<List<Servicio>> findByNombre(@RequestParam @NotBlank String nombre) {
		return new ResponseEntity<List<Servicio>>(this.servicioService.findByNombreAsc(nombre), HttpStatus.OK);
	}
}
