package com.contractar.microserviciocommons.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.contractar.microservicioadapter.enums.RoleAccesor;

public class UsuarioOauthDTO extends User {
	private static final long serialVersionUID = -5145570701186883068L;
	
	private Long id;
	
	private String name;

	private String surname;

	private String email;

	private boolean isActive;

	private RoleAccesor role;

	public UsuarioOauthDTO() {
		super("fake", "", new ArrayList<SimpleGrantedAuthority>());
	}

	public UsuarioOauthDTO(Long id, String name, String surname, String email, boolean isActive, String password,
			List<SimpleGrantedAuthority> authorities, RoleAccesor role) {
		super(name + surname, password, false, true, true, true, authorities);
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.isActive = isActive;
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
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

	public RoleAccesor getRole() {
		return role;
	}

	public void setRole(RoleAccesor role) {
		this.role = role;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
