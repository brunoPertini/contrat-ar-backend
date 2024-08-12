package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

public class PaymentDTO {
	private String state;
	private LocalDate date;
	
	public PaymentDTO() {}

	public PaymentDTO(String state, LocalDate date) {
		this.state = state;
		this.date = date;
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
}
