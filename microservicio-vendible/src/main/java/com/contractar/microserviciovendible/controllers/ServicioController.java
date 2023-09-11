package com.contractar.microserviciovendible.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.ServicioDTO;
import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import com.contractar.microserviciousuario.models.Proveedor;
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
			@RequestParam(required = false) Long proveedorId) throws Exception {
		Servicio addedServicio = (Servicio) vendibleService.save(servicio, vendibleType, proveedorId);
		ServicioDTO servicioDTO = new ServicioDTO(addedServicio.getNombre());
		return new ResponseEntity<ServicioDTO>(servicioDTO, HttpStatus.CREATED);
	}
	
	@PutMapping(VendiblesControllersUrls.MODIFY_SERVICE)
	public ResponseEntity<ServicioDTO> update(@RequestBody ServicioDTO servicio,
			@PathVariable("vendibleId") Long vendibleId) throws Exception {
		String dtoFullClassName = ServicioDTO.class.getPackage().getName() + ".ServicioDTO";
		String entityFullClassName = Servicio.class.getPackage().getName() + ".Servicio"; 
		Servicio addedServicio = (Servicio) vendibleService.update(servicio, vendibleId, dtoFullClassName, entityFullClassName, vendibleType);
		Set<ProveedorVendibleDTO> proveedoresVendibles = addedServicio.getProveedoresVendibles()
				.stream()
				.map(proveedorVendible -> {
					Proveedor proveedor = proveedorVendible.getProveedor();
					ProveedorVendibleDTO proveedorVendibleDTO = new ProveedorVendibleDTO(addedServicio.getNombre(),
							proveedorVendible.getDescripcion(), 
							proveedorVendible.getPrecio(),
							proveedorVendible.getImagenUrl(),
							proveedorVendible.getStock(),
							new ProveedorDTO(proveedor));
							return proveedorVendibleDTO;
				}).collect(Collectors.toSet());
		ServicioDTO servicioDTO = new ServicioDTO(addedServicio.getNombre(),proveedoresVendibles);
		return new ResponseEntity<ServicioDTO>(servicioDTO, HttpStatus.OK);
	}
	
	@GetMapping(VendiblesControllersUrls.GET_SERVICE)
	public ResponseEntity<VendiblesResponseDTO> findByNombre(@RequestParam @NotBlank String nombre) {
		return new ResponseEntity<VendiblesResponseDTO>(this.servicioService.findByNombreAsc(nombre), HttpStatus.OK);
	}
}
