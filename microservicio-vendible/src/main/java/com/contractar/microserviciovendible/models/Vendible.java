package com.contractar.microserviciovendible.models;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="vendible_type", discriminatorType = DiscriminatorType.STRING)
@Entity
public class Vendible{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="vendible_id")
	private Long id;

	@Column(unique = true)
	private String nombre;

	private int precio;

	private String descripcion;

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

	public Vendible() {}

	public Vendible(int precio, String descripcion, String nombre) {
		this.precio = precio;
		this.descripcion = descripcion;
		this.nombre = nombre;
	}

}
