package com.contractar.microserviciopayment.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Currency;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Payment implements Serializable {
	private static final long serialVersionUID = -5539133673400232413L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long externalId;

	@NotNull
	private LocalDate paymentPeriod;

	@NotNull
	private LocalDate date;

	private int amount;

	private Currency currency;

	@OneToOne
	@JoinColumn(name = "state")
	@NotNull
	private PaymentState state;

	@OneToOne
	@JoinColumn(name = "payment_provider")
	@NotNull
	private PaymentProvider paymentProvider;

	public Payment() {
	}

	public Payment(Long externalId, LocalDate paymentPeriod, LocalDate date, int amount, Currency currency,
			PaymentProvider paymentProvider, PaymentState state) {
		this.externalId = externalId;
		this.paymentPeriod = paymentPeriod;
		this.date = date;
		this.amount = amount;
		this.currency = currency;
		this.paymentProvider = paymentProvider;
		this.state = state;
	}

	public PaymentState getState() {
		return state;
	}

	public void setState(PaymentState state) {
		this.state = state;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public LocalDate getPaymentPeriod() {
		return paymentPeriod;
	}

	public void setPaymentPeriod(LocalDate paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public PaymentProvider getPaymentProvider() {
		return paymentProvider;
	}

	public void setPaymentProvider(PaymentProvider paymentProvider) {
		this.paymentProvider = paymentProvider;
	}

}
