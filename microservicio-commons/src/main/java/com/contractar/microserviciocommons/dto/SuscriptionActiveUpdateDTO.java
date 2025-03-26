package com.contractar.microserviciocommons.dto;

public class SuscriptionActiveUpdateDTO {
	private Long id;
	private boolean isActive;

	public SuscriptionActiveUpdateDTO() {
	}

	public SuscriptionActiveUpdateDTO(Long id, boolean isActive) {
		this.id = id;
		this.isActive = isActive;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
