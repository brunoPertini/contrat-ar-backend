package com.contractar.usuarios.models;

import com.contractar.serviciocommons.plans.PlanType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import java.io.Serializable;

import org.locationtech.jts.geom.Point;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuario")
public class Usuario implements Serializable{
	private static final long serialVersionUID = -1655979560902202392L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 80)
	private String nombre;
	
	@Column(length = 100)
	private String apellido;
	
	@Column(unique= true, nullable = false)
	private String email;
	
	private boolean isActive;
	
	private Point ubicacion;
	
	public Usuario() {
		
	}

	public Usuario(Long id, String nombre, String apellido, String email, boolean isActive,
			Point ubicacion) {
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.isActive = isActive;
		this.ubicacion = ubicacion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Point getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(Point ubicacion) {
		this.ubicacion = ubicacion;
	}
}
