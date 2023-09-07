package com.contractar.microserviciousuario.models;

import java.io.Serializable;

import com.contractar.microserviciovendible.models.Vendible;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 
 * Maps the relation between Proveedor and Vendible entities. Many Proveedores can do a Vendible. However, each one
 * may do it with a different description, price, image, and so on.
 */
@Entity
public class ProveedorVendible implements Serializable{
	private static final long serialVersionUID = -2724448122568231385L;

	@EmbeddedId
	private ProveedorVendibleId id;
	
	@NotNull
	private int precio;

	@NotBlank
	private String descripcion;
	
	private String imagenUrl;
	
	private int stock;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("vendibleId")
	@JoinColumn(name = "vendible_id")
	private Vendible vendible;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("proveedorId")
	@JoinColumn(name = "proveedor_id")
	private Proveedor proveedor;
	
	public ProveedorVendible() {}
	
	public ProveedorVendible(ProveedorVendibleId id, @NotNull int precio, @NotBlank String descripcion,
			String imagenUrl, int stock, Vendible vendible, Proveedor proveedor) {
		this.id = id;
		this.precio = precio;
		this.descripcion = descripcion;
		this.imagenUrl = imagenUrl;
		this.stock = stock;
		this.vendible = vendible;
		this.proveedor = proveedor;
	}

	public ProveedorVendibleId getId() {
		return id;
	}

	public void setId(ProveedorVendibleId id) {
		this.id = id;
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

	public Vendible getVendible() {
		return vendible;
	}

	public void setVendible(Vendible vendible) {
		this.vendible = vendible;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
}
