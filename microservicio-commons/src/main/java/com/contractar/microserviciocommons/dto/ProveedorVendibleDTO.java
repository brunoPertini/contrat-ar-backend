package com.contractar.microserviciocommons.dto;

public class ProveedorVendibleDTO {
	
	private String vendibleNombre;

	private String descripcion;
	
	private int precio;
	
	private String imagenUrl;
	
	private int stock;
	
	private ProveedorDTO proveedor;

	public String getVendibleNombre() {
		return vendibleNombre;
	}

	public void setVendibleNombre(String vendibleNombre) {
		this.vendibleNombre = vendibleNombre;
	}
	
	public ProveedorVendibleDTO() {}

	public ProveedorVendibleDTO(String vendibleNombre, String descripcion, int precio, String imagenUrl, int stock,
			ProveedorDTO proveedor) {
		super();
		this.vendibleNombre = vendibleNombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.imagenUrl = imagenUrl;
		this.stock = stock;
		this.proveedor = proveedor;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public ProveedorDTO getProveedor() {
		return proveedor;
	}

	public void setProveedor(ProveedorDTO proveedor) {
		this.proveedor = proveedor;
	}
}
