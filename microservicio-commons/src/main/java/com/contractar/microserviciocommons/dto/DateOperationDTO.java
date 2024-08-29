package com.contractar.microserviciocommons.dto;

import java.time.LocalDate;

import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;

public class DateOperationDTO {
	private LocalDate date;
	private DateOperationType operation;
	private DateFormatType formatType;

	public DateOperationDTO(LocalDate date, DateOperationType operation, DateFormatType formatType) {
		this.date = date;
		this.operation = operation;
		this.formatType = formatType;
	}

	public DateOperationDTO() {
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public DateOperationType getOperation() {
		return operation;
	}

	public void setOperation(DateOperationType operation) {
		this.operation = operation;
	}

	public DateFormatType getFormatType() {
		return formatType;
	}

	public void setFormatType(DateFormatType formatType) {
		this.formatType = formatType;
	}
}
