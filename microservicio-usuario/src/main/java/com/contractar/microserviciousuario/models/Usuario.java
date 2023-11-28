package com.contractar.microserviciousuario.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.serialization.UserDetailsDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonDeserialize(using = UserDetailsDeserializer.class)
public class Usuario extends User implements Serializable {
	private static final long serialVersionUID = -1655979560902202392L;

	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 80, nullable = false)
	@NotBlank
	private String name;

	@Column(length = 100, nullable = false)
	@NotBlank
	private String surname;

	@Column(length = 200)
	private String password;

	@Column(unique = true, nullable = false)
	@NotBlank
	private String email;

	private boolean isActive;

	@NotNull
	@JsonDeserialize(using = UbicacionDeserializer.class)
	private Point location;

	@NotNull
	private LocalDate birthDate;

	@OneToOne
	@JoinColumn(name = "role")
	private Role role;

	public Usuario() {
		super("fake", "", new ArrayList<SimpleGrantedAuthority>());
	}
	
	public Usuario(String name, String surname, String email, boolean isActive, Point location,
			LocalDate birthDate, String password, List<GrantedAuthority> authorities, Role role) {
		super(name + surname, password, false, true, true, true, authorities);
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.isActive = isActive;
		this.location = location;
		this.birthDate = birthDate;
		this.password = password;
		this.role = role;
	}

	public Usuario(Long id, String name, String surname, String email, boolean isActive, Point location,
			LocalDate birthDate, String password, List<GrantedAuthority> authorities, Role role) {
		super(name + surname, password, false, true, true, true, authorities);
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.isActive = isActive;
		this.location = location;
		this.birthDate = birthDate;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	public String getsurname() {
		return surname;
	}

	public void setsurname(String surname) {
		this.surname = surname;
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

	public Point getlocation() {
		return location;
	}

	public void setlocation(Point location) {
		this.location = location;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
