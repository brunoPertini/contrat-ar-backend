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
import com.contractar.microserviciocommons.dto.ProductoDTO;
import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.ProveedorVendibleDTO;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.models.Producto;
import com.contractar.microserviciovendible.services.ProductoService;
import com.contractar.microserviciovendible.services.VendibleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Controller
public class ProductoController {
	@Autowired
	private VendibleService vendibleService;
	
	@Autowired
	private ProductoService productoService;
	
	private final String vendibleType = VendibleType.PRODUCTO.toString();
	
	@PostMapping(VendiblesControllersUrls.SAVE_PRODUCT)
	public ResponseEntity<ProductoDTO> save(@RequestBody @Valid Producto producto,
			@RequestParam(required = false) Long proveedorId) throws Exception {
		Producto addedProducto = (Producto) vendibleService.save(producto, vendibleType, proveedorId);
		ProductoDTO productoDTO = new ProductoDTO(addedProducto.getNombre());
		return new ResponseEntity<ProductoDTO>(productoDTO, HttpStatus.CREATED);
	}
	
	@PutMapping(VendiblesControllersUrls.MODIFY_PRODUCT)
	public ResponseEntity<ProductoDTO> update(@RequestBody ProductoDTO productoDTO,
			@PathVariable("vendibleId") Long vendibleId) throws Exception {
		String dtoFullClassName = ProductoDTO.class.getPackage().getName() + ".ProductoDTO"; 
		String entityFullClassName = Producto.class.getPackage().getName() + ".Producto"; 
		Producto producto = (Producto) vendibleService.update(productoDTO, vendibleId, dtoFullClassName, entityFullClassName, vendibleType);
		Set<ProveedorVendibleDTO> proveedoresVendibles = producto.getProveedoresVendibles()
				.stream()
				.map(proveedorVendible -> {
					Proveedor proveedor = proveedorVendible.getProveedor();
					ProveedorVendibleDTO proveedorVendibleDTO = new ProveedorVendibleDTO(producto.getNombre(),
							proveedorVendible.getDescripcion(), 
							proveedorVendible.getPrecio(),
							proveedorVendible.getImagenUrl(),
							proveedorVendible.getStock(),
							new ProveedorDTO(proveedor));
							return proveedorVendibleDTO;
				}).collect(Collectors.toSet());
		ProductoDTO updatedProductoDTO = new ProductoDTO(producto.getNombre(), proveedoresVendibles);
		return new ResponseEntity<ProductoDTO>(updatedProductoDTO, HttpStatus.OK);
	}
	
	@GetMapping(VendiblesControllersUrls.GET_PRODUCT)
	public ResponseEntity<List<ProductoDTO>> findByNombre(@RequestParam @NotBlank String nombre) {
		return new ResponseEntity<List<ProductoDTO>>(this.productoService.findByNombreAsc(nombre), HttpStatus.OK);
	}
}
