package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

public class SuscripcionDTO {
	private boolean isActive;
	private Long usuarioId;
	private Long planId;
	private LocalDate createdDate;

	
	public SuscripcionDTO() {}
	
	public SuscripcionDTO(boolean isActive, Long usuarioId, Long planId, LocalDate createdDate) {
		this.isActive = isActive;
		this.usuarioId = usuarioId;
		this.planId = planId;
		this.createdDate = createdDate;
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
	public LocalDate getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

}
