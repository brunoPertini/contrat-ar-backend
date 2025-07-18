package com.contractar.microserviciousuario.models;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Promotion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 150)
	private String text;

	@Column(length = 70)
	private String disclaimer;

	private BigDecimal discountPercentage;
	
	@Enumerated(EnumType.STRING)
	private PromotionType type;
	
	@Transient
	@JsonIgnore
	private static final Map<PromotionType, Integer> expirationMonthResolver= Map.of(PromotionType.FULL_DISCOUNT_FOREVER,
			-1,
			PromotionType.FULL_DISCOUNT_MONTHS,
			6);
	
	private boolean isEnabled;

	public Promotion() {
	}

	public Promotion(String text, String disclaimer, BigDecimal discountPercentage) {
		this.text = text;
		this.disclaimer = disclaimer;
		this.discountPercentage = discountPercentage;
		this.isEnabled = true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
	public PromotionType getType() {
		return type;
	}

	public void setType(PromotionType type) {
		this.type = type;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	/**
	 * 
	 * @return duration in months for this promotion to expire
	 */
	public  int getExpirationMonths() {
		return expirationMonthResolver.get(this.type);
	}

}
