package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotBlank;

public class PaymentLinkMailInfo extends MailInfo {
	@NotBlank
	private String userName;

	@NotBlank
	private String paymentLink;

	public PaymentLinkMailInfo(@NotBlank String toAddress, @NotBlank String userName, @NotBlank String paymentLink) {
		super(toAddress);
		this.userName = userName;
		this.paymentLink = paymentLink;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPaymentLink() {
		return paymentLink;
	}

	public void setPaymentLink(String paymentLink) {
		this.paymentLink = paymentLink;
	}

}
