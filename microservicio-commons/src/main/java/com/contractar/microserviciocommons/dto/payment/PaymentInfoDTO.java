package com.contractar.microserviciocommons.dto.payment;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Currency;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"paymentPeriod", "date", "amount", "currency", "state", "paymentProviderName"})
public class PaymentInfoDTO {
	private Long id;

	private String externalId;

	@JsonFormat(pattern = "MM/yyyy")
	private YearMonth paymentPeriod;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate date;

	private int amount;

	private Currency currency;

	private String state;

	private String paymentProviderName;

	public PaymentInfoDTO() {
	}

	public PaymentInfoDTO(Long id, String externalId, YearMonth paymentPeriod, LocalDate date, int amount,
			Currency currency, String state, String paymentProviderName) {
		this.id = id;
		this.externalId = externalId;
		this.paymentPeriod = paymentPeriod;
		this.date = date;
		this.amount = amount;
		this.currency = currency;
		this.state = state;
		this.paymentProviderName = paymentProviderName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPaymentProviderName() {
		return paymentProviderName;
	}

	public void setPaymentProviderName(String paymentProviderName) {
		this.paymentProviderName = paymentProviderName;
	}

}
