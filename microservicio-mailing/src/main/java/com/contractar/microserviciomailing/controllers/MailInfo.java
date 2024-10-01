package com.contractar.microserviciomailing.controllers;

public final class MailInfo {
	private String toAddress;
	private String title;
	private String message;

	public MailInfo() {

	}

	public MailInfo(String toAddress, String title, String message) {
		this.toAddress = toAddress;
		this.title = title;
		this.message = message;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
