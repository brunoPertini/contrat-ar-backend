package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"datePattern"})
public class SuscripcionDTO extends WithDatePatternDTO {
	private Long id;
	private boolean isActive;
	private Long usuarioId;
	private Long planId;
	private int planPrice;
	
	private SuscriptionValidityDTO validity;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate createdDate;
	
	private UserPromotionDTO promotionInfo;

	public SuscripcionDTO() {
	}

	public SuscripcionDTO(Long id, boolean isActive, Long usuarioId, Long planId, LocalDate createdDate) {
		super();
		this.id = id;
		this.isActive = isActive;
		this.usuarioId = usuarioId;
		this.planId = planId;
		this.createdDate = createdDate;
	}
	
	public SuscripcionDTO(Long id, boolean isActive, Long usuarioId, Long planId, LocalDate createdDate, int planPrice) {
		super();
		this.id = id;
		this.isActive = isActive;
		this.usuarioId = usuarioId;
		this.planId = planId;
		this.createdDate = createdDate;
		this.planPrice = planPrice;
	}

	public SuscripcionDTO(Long id, boolean isActive, Long usuarioId, Long planId, LocalDate createdDate,
			String datePattern) {
		super(datePattern);
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

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public int getPlanPrice() {
		return planPrice;
	}

	public void setPlanPrice(int planPrice) {
		this.planPrice = planPrice;
	}
	
	public SuscriptionValidityDTO getValidity() {
		return validity;
	}

	public void setValidity(SuscriptionValidityDTO validity) {
		this.validity = validity;
	}
	
	public UserPromotionDTO getPromotionInfo() {
		return promotionInfo;
	}

	public void setPromotionInfo(UserPromotionDTO promotionInfo) {
		this.promotionInfo = promotionInfo;
	}

}
