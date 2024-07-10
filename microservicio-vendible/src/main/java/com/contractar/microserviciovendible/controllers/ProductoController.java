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
import com.contractar.microserviciocommons.dto.ProductoDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleUpdateDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.CantCreateException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyExistsException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import com.contractar.microserviciousuario.models.Producto;
import com.contractar.microserviciovendible.services.VendibleService;
import com.contractar.microserviciovendible.services.resolvers.ProductoFetchingMethodResolver;

import jakarta.validation.Valid;

@Controller
public class ProductoController {
	@Autowired
	private VendibleService vendibleService;
	
	@Autowired
	private ProductoFetchingMethodResolver productoFetchingMethodResolver;

	private final String vendibleType = VendibleType.PRODUCTO.toString();

	@PostMapping(VendiblesControllersUrls.SAVE_PRODUCT)
	public ResponseEntity<ProductoDTO> save(@RequestBody @Valid Producto producto,
			@RequestParam(required = false) Long proveedorId) throws VendibleAlreadyExistsException, UserNotFoundException, CantCreateException {
		Producto addedProducto = (Producto) vendibleService.save(producto, vendibleType, proveedorId);
		ProductoDTO productoDTO = new ProductoDTO(addedProducto.getNombre());
		return new ResponseEntity<ProductoDTO>(productoDTO, HttpStatus.CREATED);
	}

	@PutMapping(VendiblesControllersUrls.MODIFY_PRODUCT)
	public ResponseEntity<ProductoDTO> update(@RequestBody @Valid VendibleUpdateDTO body,
			@PathVariable("vendibleId") Long vendibleId) throws VendibleNotFoundException {
		Producto producto = (Producto) vendibleService.update(body.getNombre(), vendibleId, vendibleType);
		ProductoDTO updatedProductoDTO = new ProductoDTO(producto.getNombre());
		return new ResponseEntity<ProductoDTO>(updatedProductoDTO, HttpStatus.OK);
	}

	@GetMapping(VendiblesControllersUrls.GET_PRODUCT)
	public ResponseEntity<VendiblesResponseDTO> findByNombre(@RequestParam(required = false) String nombre,
			@RequestParam(required = false) Long category) {
		return new ResponseEntity<VendiblesResponseDTO>(this.vendibleService.findByNombreAsc(nombre, category, productoFetchingMethodResolver), HttpStatus.OK);
	}
}
