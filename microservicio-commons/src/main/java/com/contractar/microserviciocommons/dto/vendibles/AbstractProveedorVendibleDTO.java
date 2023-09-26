package com.contractar.microserviciocommons.dto.vendibles;

public abstract class AbstractProveedorVendibleDTO {
	private String vendibleNombre;

	private String descripcion;

	private int precio;

	private String imagenUrl;

	private int stock;
	
	private Long vendibleCategoryId;

	public AbstractProveedorVendibleDTO(String vendibleNombre, String descripcion, int precio, String imagenUrl,
			int stock) {
		this.vendibleNombre = vendibleNombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.imagenUrl = imagenUrl;
		this.stock = stock;
	}

	public AbstractProveedorVendibleDTO() {
	}

	public String getVendibleNombre() {
		return vendibleNombre;
	}

	public void setVendibleNombre(String vendibleNombre) {
		this.vendibleNombre = vendibleNombre;
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
	
	public Long getVendibleCategoryId() {
		return vendibleCategoryId;
	}

	public void setVendibleCategoryId(Long vendibleCategoryId) {
		this.vendibleCategoryId = vendibleCategoryId;
	}

}
