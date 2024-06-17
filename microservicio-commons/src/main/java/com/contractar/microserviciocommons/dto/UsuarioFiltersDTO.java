package com.contractar.microserviciocommons.dto;

public class UsuarioFiltersDTO {
	private String name;
	private String surname;
	private String email;
	private Long plan;
	
	public UsuarioFiltersDTO() {
	}

	public UsuarioFiltersDTO(String name, String surname, String email, Long plan) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.plan = plan;
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
	
	public Long getPlan() {
		return plan;
	}

	public void setPlan(Long plan) {
		this.plan = plan;
	}

}
