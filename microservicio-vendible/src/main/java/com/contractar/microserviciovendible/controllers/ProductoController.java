package com.contractar.microserviciovendible.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.ProductoDTO;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import com.contractar.microserviciovendible.models.Producto;
import com.contractar.microserviciovendible.services.VendibleService;

import jakarta.validation.Valid;

@Controller
public class ProductoController {
	@Autowired
	private VendibleService vendibleService;
	
	@PostMapping(VendiblesControllersUrls.SAVE_PRODUCT)
	public ResponseEntity<ProductoDTO> save(@RequestBody @Valid Producto producto,
			@RequestParam(required = true) Long proveedorId) throws Exception {
		Producto addedProducto = (Producto) vendibleService.save(producto, VendibleType.PRODUCTO.toString(), proveedorId);
		ProductoDTO productoDTO = new ProductoDTO(addedProducto.getNombre(),
				addedProducto.getPrecio(),
				addedProducto.getDescripcion(),
				addedProducto.getImagesUrl(),
				addedProducto.getProveedores(),
				addedProducto.getStock());
		return new ResponseEntity<ProductoDTO>(productoDTO, HttpStatus.CREATED);
	} 
}
