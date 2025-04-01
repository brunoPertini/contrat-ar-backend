package com.contractar.microserviciocommons.dto.payment;

import java.util.List;
import java.util.Map;

public class PaymentsResponseDTO {
	private Map<String, String> states;
	
	private List<PaymentInfoDTO> payments;
	
	public PaymentsResponseDTO() {}

	public PaymentsResponseDTO(Map<String, String> states, List<PaymentInfoDTO> payments) {
		this.states = states;
		this.payments = payments;
	}

	public Map<String, String> getStates() {
		return states;
	}

	public void setStates(Map<String, String> states) {
		this.states = states;
	}

	public List<PaymentInfoDTO> getPayments() {
		return payments;
	}

	public void setPayments(List<PaymentInfoDTO> payments) {
		this.payments = payments;
	}

}
