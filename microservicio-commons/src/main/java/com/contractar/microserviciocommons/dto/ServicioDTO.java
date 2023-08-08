package com.contractar.microserviciocommons.dto;

import java.util.List;

import java.util.stream.Collectors;

import com.contractar.microserviciousuario.models.Proveedor;

public class ServicioDTO extends EntityDTO {
	private String nombre;
	private int precio;
	private String descripcion;
	private List<String> imagesUrl;
	private List<Long> proveedoresIds;
	
	
	public ServicioDTO() {
	}
	public ServicioDTO(String nombre, int precio, String descripcion, List<String> imagesUrl,
			List<Proveedor> proveedores) {
		super();
		this.nombre = nombre;
		this.precio = precio;
		this.descripcion = descripcion;
		this.imagesUrl = imagesUrl;
		this.proveedoresIds = proveedores.stream().map(proveedor -> proveedor.getId()).collect(Collectors.toList());
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getPrecio() {
		return precio;
	}
	public void setPrecio(int precio) {
		this.precio = precio;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public List<String> getImagesUrl() {
		return imagesUrl;
	}
	public void setImagesUrl(List<String> imagesUrl) {
		this.imagesUrl = imagesUrl;
	}
	public List<Long> getProveedoresIds() {
		return proveedoresIds;
	}
	public void setProveedoresIds(List<Long> proveedoresIds) {
		this.proveedoresIds = proveedoresIds;
	}
}
