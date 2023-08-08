package com.contractar.microserviciocommons.dto;

import java.util.List;

import com.contractar.microserviciousuario.models.Proveedor;

public class ServicioDTO extends VendibleDTO {
	public ServicioDTO(String nombre, int precio, String descripcion, List<String> imagesUrl,
			List<Proveedor> proveedores) {
		super(nombre, precio, descripcion, imagesUrl, proveedores);
	}

}
