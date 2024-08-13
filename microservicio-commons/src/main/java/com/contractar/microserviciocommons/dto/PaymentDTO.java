package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

public class PaymentDTO {
	private String state;
	private LocalDate date;
	private LocalDate paymentPeriod;

	public PaymentDTO() {
	}

	public PaymentDTO(String state, LocalDate date, LocalDate paymentPeriod) {
		this.state = state;
		this.date = date;
		this.paymentPeriod = paymentPeriod;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalDate getPaymentPeriod() {
		return paymentPeriod;
	}

	public void setPaymentPeriod(LocalDate paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
	}
}
