package com.contractar.microserviciocommons.dto;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciousuario.models.Proveedor;

public abstract class VendibleDTO {
	private String nombre;
	private int precio;
	private String descripcion;
	private Set<String> imagesUrl;
	private List<ProveedorDTO> proveedores;

	public VendibleDTO(String nombre, int precio, String descripcion, Set<String> imagesUrl,
			List<Proveedor> proveedores) {
		this.nombre = nombre;
		this.precio = precio;
		this.descripcion = descripcion;
		this.imagesUrl = imagesUrl;
		this.proveedores = proveedores != null ? 
				proveedores.stream().map(proveedor -> {
					String dtoFullClassName = ProveedorDTO.class.getPackage().getName() + ".ProveedorDTO";
					String entityFullClassName = Proveedor.class.getPackage().getName() + ".Proveedor";
					ProveedorDTO proveedorDTO = new ProveedorDTO();
					try {
						ReflectionHelper.applySetterFromExistingFields(proveedor, proveedorDTO, entityFullClassName,dtoFullClassName);
					} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						return proveedorDTO;
					}
					return proveedorDTO;
				}).collect(Collectors.toList())
				: new ArrayList<>();
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

	public Set<String> getImagesUrl() {
		return imagesUrl;
	}

	public void setImagesUrl(Set<String> imagesUrl) {
		this.imagesUrl = imagesUrl;
	}

	public List<ProveedorDTO> getProveedores() {
		return proveedores;
	}

	public void setProveedores(List<ProveedorDTO> proveedores) {
		this.proveedores = proveedores;
	}
}
