package com.contractar.microserviciocommons.dto;

import java.util.LinkedHashSet;
import java.util.Set;

import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleDTO;

public class ProductoDTO extends VendibleDTO {
	
	public ProductoDTO(String nombre) {
		super(nombre, new LinkedHashSet<ProveedorVendibleDTO>());
	}
	
	public ProductoDTO(String nombre, Set<ProveedorVendibleDTO> proveedores) {
		super(nombre, proveedores);
	}
}
