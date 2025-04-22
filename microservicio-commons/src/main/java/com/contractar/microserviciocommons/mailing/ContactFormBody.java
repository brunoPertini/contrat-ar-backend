package com.contractar.microserviciocommons.mailing;

public class ContactFormBody {
	private String fromName;

	private String fromEmail;

	private String phoneField;

	private String message;

	public ContactFormBody(String fromName, String fromEmail, String phoneField, String message) {
		this.fromName = fromName;
		this.fromEmail = fromEmail;
		this.phoneField = phoneField;
		this.message = message;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getPhoneField() {
		return phoneField;
	}

	public void setPhoneField(String phoneField) {
		this.phoneField = phoneField;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
