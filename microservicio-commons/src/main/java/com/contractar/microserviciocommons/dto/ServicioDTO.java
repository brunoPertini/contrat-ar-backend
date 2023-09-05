package com.contractar.microserviciocommons.dto;

import java.util.LinkedHashSet;
import java.util.Set;

public class ServicioDTO extends VendibleDTO {
	public ServicioDTO(String nombre) {
		super(nombre, new LinkedHashSet<ProveedorVendibleDTO>());
	}
	
	public ServicioDTO(String nombre, Set<ProveedorVendibleDTO> proveedores) {
		super(nombre, proveedores);
	}

}
