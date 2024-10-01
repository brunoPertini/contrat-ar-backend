package com.contractar.microserviciomailing.controllers;

public final class MailInfo {
	private String toAddress;

	public MailInfo() {

	}

	public MailInfo(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

}
