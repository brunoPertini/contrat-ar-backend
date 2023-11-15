package com.contractar.microserviciocommons.dto.proveedorvendible;

public class ProveedorVendibleUpdateDTO {
	
	private String descripcion;
	private String imagenUrl;
	private int precio;
	private int stock;
	
	public ProveedorVendibleUpdateDTO(String descripcion, String imagenUrl, int precio, int stock) {
		this.descripcion = descripcion;
		this.imagenUrl = imagenUrl;
		this.precio = precio;
		this.stock = stock;
	}

	public ProveedorVendibleUpdateDTO() {}

	public String getDescripcion() {
		return descripcion;
	}



	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}



	public String getImagenUrl() {
		return imagenUrl;
	}



	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}



	public int getPrecio() {
		return precio;
	}



	public void setPrecio(int precio) {
		this.precio = precio;
	}



	public int getStock() {
		return stock;
	}



	public void setStock(int stock) {
		this.stock = stock;
	}
	
}
