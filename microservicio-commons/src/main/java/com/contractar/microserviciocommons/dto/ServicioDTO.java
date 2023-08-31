package com.contractar.microserviciocommons.dto;

import java.util.List;
import java.util.Set;

import com.contractar.microserviciousuario.models.Proveedor;

public class ServicioDTO extends VendibleDTO {
	public ServicioDTO(String nombre, int precio, String descripcion, Set<String> imagesUrl,
			List<Proveedor> proveedores) {
		super(nombre, precio, descripcion, imagesUrl, proveedores);
	}

}
