package com.contractar.microserviciovendible.models;

import java.io.Serializable;
import java.util.List;

import com.contractar.usuarios.models.Proveedor;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendible")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="vendible_type", discriminatorType = DiscriminatorType.STRING)
public class Vendible implements Serializable {

	private static final long serialVersionUID = -4955873418985503685L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String nombre;
	
	private int precio;
	
	private String descripcion;
	
	@ManyToMany(mappedBy = "vendibles")
	private List<Proveedor> proveedores;

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
	
	public Vendible() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Vendible(int precio, String descripcion, String nombre) {
		super();
		this.precio = precio;
		this.descripcion = descripcion;
		this.nombre = nombre;
	}

}
