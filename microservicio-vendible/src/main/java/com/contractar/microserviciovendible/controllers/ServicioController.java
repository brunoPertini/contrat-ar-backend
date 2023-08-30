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

import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.ServicioDTO;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.services.ServicioService;
import com.contractar.microserviciovendible.services.VendibleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Controller
public class ServicioController {
	@Autowired
	private ServicioService servicioService;
	
	@Autowired
	private VendibleService vendibleService;
	
	private final String vendibleType = VendibleType.SERVICIO.toString();
	
	@PostMapping(VendiblesControllersUrls.SAVE_SERVICE)
	public ResponseEntity<ServicioDTO> save(@RequestBody @Valid Servicio servicio,
			@RequestParam(required = true) Long proveedorId) throws Exception {
		Servicio addedServicio = (Servicio) vendibleService.save(servicio, vendibleType, proveedorId);
		ServicioDTO servicioDTO = new ServicioDTO(addedServicio.getNombre(),
				addedServicio.getPrecio(),
				addedServicio.getDescripcion(),
				addedServicio.getImagesUrl(),
				addedServicio.getProveedores());
		return new ResponseEntity<ServicioDTO>(servicioDTO, HttpStatus.CREATED);
	}
	
	@PutMapping(VendiblesControllersUrls.MODIFY_SERVICE)
	public ResponseEntity<ServicioDTO> update(@RequestBody ServicioDTO servicio,
			@PathVariable("vendibleId") Long vendibleId) throws Exception {
		String dtoFullClassName = ServicioDTO.class.getPackage().getName() + ".ServicioDTO";
		String entityFullClassName = Servicio.class.getPackage().getName() + ".Servicio"; 
		Servicio addedServicio = (Servicio) vendibleService.update(servicio, vendibleId, dtoFullClassName, entityFullClassName, vendibleType);
		ServicioDTO servicioDTO = new ServicioDTO(addedServicio.getNombre(),
				addedServicio.getPrecio(),
				addedServicio.getDescripcion(),
				addedServicio.getImagesUrl(),
				addedServicio.getProveedores());
		return new ResponseEntity<ServicioDTO>(servicioDTO, HttpStatus.OK);
	}
	
	@GetMapping(VendiblesControllersUrls.GET_SERVICE)
	public ResponseEntity<List<ServicioDTO>> findByNombre(@RequestParam @NotBlank String nombre) {
		return new ResponseEntity<List<ServicioDTO>>(this.servicioService.findByNombreAsc(nombre), HttpStatus.OK);
	}
}
