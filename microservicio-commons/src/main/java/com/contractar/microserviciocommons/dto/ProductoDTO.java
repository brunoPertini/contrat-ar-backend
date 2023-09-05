package com.contractar.microserviciocommons.dto;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProductoDTO extends VendibleDTO {
	
	public ProductoDTO(String nombre) {
		super(nombre, new LinkedHashSet<ProveedorVendibleDTO>());
	}
	
	public ProductoDTO(String nombre, Set<ProveedorVendibleDTO> proveedores) {
		super(nombre, proveedores);
	}
}
