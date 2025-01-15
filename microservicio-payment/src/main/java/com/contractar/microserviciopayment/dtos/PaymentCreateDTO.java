package com.contractar.microserviciopayment.dtos;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Currency;

import com.contractar.microserviciocommons.serialization.YearMonthDeserializer;
import com.contractar.microserviciocommons.serialization.YearMonthSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotNull;

public class PaymentCreateDTO {
	private String externalId;

	@JsonDeserialize(using = YearMonthDeserializer.class)
	@JsonSerialize(using = YearMonthSerializer.class)
	@NotNull
	private YearMonth paymentPeriod;

	private int amount;

	private Currency currency;

	private Long providerId;

	private LocalDate date;

	public PaymentCreateDTO() {
	}

	public PaymentCreateDTO(String externalId, @NotNull YearMonth paymentPeriod, int amount, Currency currency,
			Long providerId, LocalDate date) {
		this.externalId = externalId;
		this.paymentPeriod = paymentPeriod;
		this.amount = amount;
		this.currency = currency;
		this.providerId = providerId;
		this.date = date;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public YearMonth getPaymentPeriod() {
		return paymentPeriod;
	}

	public void setPaymentPeriod(YearMonth paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
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

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}
