package com.contractar.microserviciovendible.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.contractar.microserviciousuario.models.Proveedor;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="vendible_type", discriminatorType = DiscriminatorType.STRING)
@Entity
public class Vendible implements Serializable{

	private static final long serialVersionUID = -6708815378872073493L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="vendible_id")
	private Long id;

	@Column(unique = true)
	@NotBlank
	private String nombre;

	@NotNull
	private int precio;

	@NotBlank
	private String descripcion;
		
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "vendible_images", joinColumns = @JoinColumn(name = "vendible_id", nullable = false))
	@Column(name = "image_url", nullable = false)
	private Set<String> imagesUrl;
	 
	
	@ManyToMany(mappedBy = "vendibles",
			targetEntity = Proveedor.class,
			fetch = FetchType.LAZY)
	private List<Proveedor> proveedores;

	public List<Proveedor> getProveedores() {
		return proveedores;
	}

	public void setProveedores(List<Proveedor> proveedores) {
		this.proveedores = proveedores;
	}

	public Set<String> getImagesUrl() {
		return imagesUrl;
	}

	public void setImagesUrl(Set<String> imagesUrl) {
		this.imagesUrl = imagesUrl;
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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	} 

	public Vendible() {
		this.proveedores = new ArrayList<Proveedor>();
	}
	
	public Vendible(int precio, String descripcion, String nombre) {
		this.precio = precio;
		this.descripcion = descripcion;
		this.nombre = nombre;
	}

	public Vendible(int precio, String descripcion, String nombre, List<Proveedor> proveedores) {
		this.precio = precio;
		this.descripcion = descripcion;
		this.nombre = nombre;
		this.proveedores = proveedores;
	}
}
