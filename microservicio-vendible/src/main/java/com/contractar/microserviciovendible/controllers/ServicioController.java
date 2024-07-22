package com.contractar.microserviciovendible.controllers;

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

import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.ServicioDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleUpdateDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.CantCreateException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyExistsException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import com.contractar.microserviciousuario.models.Servicio;
import com.contractar.microserviciovendible.services.VendibleService;
import com.contractar.microserviciovendible.services.resolvers.ServicioFetchingMethodResolver;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Controller
public class ServicioController {
	@Autowired
	private VendibleService vendibleService;
	
	@Autowired
	private ServicioFetchingMethodResolver servicioFetchingMethodResolver;

	private final String vendibleType = VendibleType.SERVICIO.toString();

	@PostMapping(VendiblesControllersUrls.SAVE_SERVICE)
	public ResponseEntity<ServicioDTO> save(@RequestBody @Valid Servicio servicio,
			@RequestParam(required = false) Long proveedorId) throws VendibleAlreadyExistsException,
	UserNotFoundException, CantCreateException {
		Servicio addedServicio = (Servicio) vendibleService.save(servicio, vendibleType, proveedorId);
		ServicioDTO servicioDTO = new ServicioDTO(addedServicio.getNombre());
		return new ResponseEntity<ServicioDTO>(servicioDTO, HttpStatus.CREATED);
	}

	@PutMapping(VendiblesControllersUrls.MODIFY_SERVICE)
	public ResponseEntity<ServicioDTO> update(@RequestBody VendibleUpdateDTO body,
			@PathVariable("vendibleId") Long vendibleId) throws VendibleNotFoundException {
		Servicio servicio = (Servicio) vendibleService.update(body.getNombre(), vendibleId, vendibleType);
		ServicioDTO updatedProductoDTO = new ServicioDTO(servicio.getNombre());
		return new ResponseEntity<ServicioDTO>(updatedProductoDTO, HttpStatus.OK);
	}

	@GetMapping(VendiblesControllersUrls.GET_SERVICE)
	public ResponseEntity<VendiblesResponseDTO> findByNombre(@RequestParam(required = false) String nombre,
			@RequestParam(required = false) Long category) {
		return new ResponseEntity<VendiblesResponseDTO>(this.vendibleService.findByNombreAsc(nombre,
				category,
				servicioFetchingMethodResolver), HttpStatus.OK);
	}
}
