package com.contractar.microserviciousuario.models;

import java.io.Serializable;

import org.locationtech.jts.geom.Point;
import com.contractar.serviciocommons.usuarios.UbicacionDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import jakarta.validation.constraints.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario implements Serializable{
	private static final long serialVersionUID = -1655979560902202392L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 80, nullable = false)
	@NotBlank
	private String nombre;

	@Column(length = 100, nullable = false)
	@NotBlank
	private String apellido;

	@Column(length = 40)
	private String password;

	@Column(unique= true, nullable = false)
	@NotBlank
	private String email;

	private boolean isActive;	

	@NotNull
	@JsonDeserialize(using = UbicacionDeserializer.class)
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
