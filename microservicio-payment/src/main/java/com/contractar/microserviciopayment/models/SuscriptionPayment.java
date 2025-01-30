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
