package com.contractar.microserviciocommons.dto;

import java.util.List;
import java.util.Set;

import com.contractar.microserviciousuario.models.Proveedor;

public class ProductoDTO extends VendibleDTO {
	private int stock;

	public ProductoDTO(String nombre, int precio, String descripcion, Set<String> imagesUrl,
			List<Proveedor> proveedores, int stock) {
		super(nombre, precio, descripcion, imagesUrl, proveedores);
		this.stock = stock;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
}
