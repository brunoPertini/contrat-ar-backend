package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SuscripcionDTO {
	private Long id;
	private boolean isActive;
	private Long usuarioId;
	private Long planId;
	private LocalDate createdDate;

	
	public SuscripcionDTO() {}
	
	public SuscripcionDTO(Long id, boolean isActive, Long usuarioId, Long planId, LocalDate createdDate) {
		this.id = id;
		this.isActive = isActive;
		this.usuarioId = usuarioId;
		this.planId = planId;
		this.createdDate = createdDate;
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
	public Long getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") 
	public LocalDate getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

}
