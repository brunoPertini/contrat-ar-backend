package com.contractar.microserviciopayment.dtos;

import java.time.YearMonth;
import java.util.Currency;

import com.contractar.microserviciocommons.serialization.YearMonthDeserializer;
import com.contractar.microserviciocommons.serialization.YearMonthSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotNull;

public class PaymentCreateDTO {
	private Long externalId;
	
    @JsonDeserialize(using = YearMonthDeserializer.class)
    @JsonSerialize(using = YearMonthSerializer.class)
	@NotNull
	private YearMonth paymentPeriod;
	
	private int amount;

	private Currency currency;
	
	private Long providerId;
	
	public PaymentCreateDTO() {}

	public PaymentCreateDTO(Long externalId, @NotNull YearMonth paymentPeriod, int amount, Currency currency,
			Long providerId) {
		this.externalId = externalId;
		this.paymentPeriod = paymentPeriod;
		this.amount = amount;
		this.currency = currency;
		this.providerId = providerId;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
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
	
	
}
