package com.contractar.microserviciopayment.dtos;

import java.util.Currency;

public class PaymentInfoDTO {
	private Long id;
	private String externalId;
	private int amount;
	private Currency currency;
	private String state;
	
	public PaymentInfoDTO() {}
	
	
	public PaymentInfoDTO(Long id, String externalId, int amount, Currency currency, String state) {
		this.id = id;
		this.externalId = externalId;
		this.amount = amount;
		this.currency = currency;
		this.state = state;
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
}
