package com.contractar.microserviciovendible.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.contractar.microserviciocommons.dto.ServicioDTO;
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
			@RequestParam(required = true) Long proveedorId) throws Exception {
		return new ResponseEntity<Servicio>(servicioService.save(servicio, proveedorId), HttpStatus.CREATED);
	}
	
	@PutMapping("/service/{vendibleId}")
	public ResponseEntity<ServicioDTO> update(@RequestBody ServicioDTO servicio,
			@PathVariable("vendibleId") Long vendibleId) throws Exception {
		return new ResponseEntity<ServicioDTO>(servicioService.update(servicio, vendibleId), HttpStatus.OK);
	}
	
	@GetMapping("/service")
	public ResponseEntity<List<ServicioDTO>> findByNombre(@RequestParam @NotBlank String nombre) {
		return new ResponseEntity<List<ServicioDTO>>(this.servicioService.findByNombreAsc(nombre), HttpStatus.OK);
	}
}
