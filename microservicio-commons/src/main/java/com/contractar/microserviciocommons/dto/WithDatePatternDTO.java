package com.contractar.microserviciocommons.dto;

public class WithDatePatternDTO {
	private String datePattern;
	
	public WithDatePatternDTO() {
		this.datePattern = "dd/MM/yyyy";
	}

	public WithDatePatternDTO(String datePattern) {
		this.datePattern = datePattern;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}
}
