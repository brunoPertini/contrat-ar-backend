package com.contractar.microserviciocommons.dto;

import java.util.Set;

public class ProductoDTO extends VendibleDTO {
	private int stock;

	public ProductoDTO(String nombre, Set<ProveedorVendibleDTO> proveedores, int stock) {
		super(nombre, proveedores);
		this.stock = stock;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
}
