package com.contractar.microserviciousuario.models;

import java.io.Serializable;

import org.locationtech.jts.geom.Point;

import com.contractar.microservicioadapter.entities.ProveedorVendibleAccesor;
import com.contractar.microservicioadapter.entities.VendibleAccesor;
import com.contractar.microservicioadapter.entities.VendibleCategoryAccesor;
import com.contractar.microservicioadapter.enums.PostState;
import com.contractar.microserviciocommons.constants.PriceType.PriceTypeValue;
import com.contractar.microserviciocommons.dto.vendibles.CategorizableObject;
import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 
 * Maps the relation between Proveedor and Vendible entities. Many Proveedores
 * can do a Vendible. However, each one may do it with a different description,
 * price, image, and so on.
 */
@Entity
public class ProveedorVendible implements Serializable, CategorizableObject, ProveedorVendibleAccesor {
	private static final long serialVersionUID = -2724448122568231385L;

	@EmbeddedId
	private ProveedorVendibleId id;

	@NotNull
	private int precio;

	@Enumerated(EnumType.STRING)
	@NotNull
	private PriceTypeValue tipoPrecio;

	@NotBlank
	@Column(length = 3000)
	private String descripcion;

	@NotBlank
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

	@JsonDeserialize(using = UbicacionDeserializer.class)
	@JsonSerialize(using = UbicacionSerializer.class)
	@NotNull
	private Point location;

	@NotNull
	@Column(columnDefinition = "BOOLEAN DEFAULT 0")
	private boolean offersDelivery;
	
	@NotNull
	@Column(columnDefinition = "BOOLEAN DEFAULT 0")
	private boolean offersInCustomAddress;

	@OneToOne
	@NotNull
	private VendibleCategory category;
	
	@Enumerated(EnumType.STRING)
	private PostState state;

	public ProveedorVendible() {
	}

	public ProveedorVendible(ProveedorVendibleId id, @NotNull int precio, @NotBlank String descripcion,
			String imagenUrl, int stock, Vendible vendible, Proveedor proveedor, PriceTypeValue tipoPrecio,
			VendibleCategory category, boolean offersDelivery) {
		this.id = id;
		this.precio = precio;
		this.descripcion = descripcion;
		this.imagenUrl = imagenUrl;
		this.stock = stock;
		this.vendible = vendible;
		this.proveedor = proveedor;
		this.tipoPrecio = tipoPrecio;
		this.category = category;
		this.offersDelivery = offersDelivery;
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

	public VendibleAccesor getVendible() {
		return vendible;
	}
	
	@JsonSetter
	public void setVendible(Vendible vendible) {
		this.vendible = vendible;
	}

	public void setVendible(VendibleAccesor vendible) {
		this.vendible = (Vendible)vendible;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public PriceTypeValue getTipoPrecio() {
		return tipoPrecio;
	}

	public void setTipoPrecio(PriceTypeValue tipoPrecio) {
		this.tipoPrecio = tipoPrecio;
	}

	public boolean getOffersDelivery() {
		return offersDelivery;
	}

	public void setOffersDelivery(boolean offersDelivery) {
		this.offersDelivery = offersDelivery;
	}
	
	public boolean getOffersInCustomAddress() {
		return offersInCustomAddress;
	}

	public void setOffersInCustomAddress(boolean offersInCustomAddress) {
		this.offersInCustomAddress = offersInCustomAddress;
	}

	@Override
	public VendibleCategoryAccesor getCategory() {
		return this.category;
	}
	
	@JsonSetter
	public void setCategory(VendibleCategory category) {
		this.category = category;
	}
	

	@Override
	public void setCategory(VendibleCategoryAccesor category) {
		this.category = (VendibleCategory) category;
	}

	@Override
	public void setState(PostState state) {
		this.state = state;		
	}

	@Override
	public PostState getState() {
		return state;
	}

	@Override
	public String getVendibleType() {
		return this.getVendible().getVendibleType();
	}

}
