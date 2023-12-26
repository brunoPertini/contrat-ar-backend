package com.contractar.microserviciocommons.dto.proveedorvendible;

import org.locationtech.jts.geom.Point;

import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ProveedorVendibleUpdateDTO {
	
	private String descripcion;
	private String imagenUrl;
	private int precio;
	private int stock;

	@JsonDeserialize(using = UbicacionDeserializer.class)
	@JsonSerialize(using = UbicacionSerializer.class)
	private Point location;

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
	
	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}
	
}
