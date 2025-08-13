package com.contractar.microserviciopayment.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Currency;

import com.contractar.microserviciousuario.models.Suscripcion;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class SuscriptionPayment extends Payment {

	private static final long serialVersionUID = 651020835169524762L;

	@OneToOne
	@NotNull
	private Suscripcion suscripcion;

	private LocalDateTime linkCreationTime;

	// For outsite providers
	private String paymentUrl;
	
	// Used for the cases when a user changes its plan. As its not yet binded in proveedor side, have to persist it to not be lost.
	private Long userId;
	
	// Used to delete logically the promotion instance linked to the subscription.
	private Long promotionId;

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Long getuserId() {
		return userId;
	}

	public void setuserId(Long userId) {
		this.userId = userId;
	}

	public String getPaymentUrl() {
		return paymentUrl;
	}

	public void setPaymentUrl(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}

	public Suscripcion getSuscripcion() {
		return suscripcion;
	}

	public void setSuscripcion(Suscripcion suscripcion) {
		this.suscripcion = suscripcion;
	}

	public LocalDateTime getLinkCreationTime() {
		return linkCreationTime;
	}

	public void setLinkCreationTime(LocalDateTime linkCreationTime) {
		this.linkCreationTime = linkCreationTime;
	}

	public SuscriptionPayment() {
	}

	public SuscriptionPayment(String externalId, YearMonth paymentPeriod, LocalDate date, int amount, Currency currency,
			PaymentProvider paymentProvider, PaymentState state) {
		super(externalId, paymentPeriod, date, amount, currency, paymentProvider, state);
	}

}
