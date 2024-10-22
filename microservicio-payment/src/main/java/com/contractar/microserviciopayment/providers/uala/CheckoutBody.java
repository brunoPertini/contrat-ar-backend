package com.contractar.microserviciopayment.providers.uala;

import com.contractar.microserviciopayment.serialization.UalaCheckoutBodySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = UalaCheckoutBodySerializer.class)
public class CheckoutBody {
	private int amount;
	private String description;
	private String userName;

	private String callbackFail;

	private String callbackSuccess;

	private String notificationUrl;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCallbackFail() {
		return callbackFail;
	}

	public void setCallbackFail(String callbackFail) {
		this.callbackFail = callbackFail;
	}

	public String getCallbackSuccess() {
		return callbackSuccess;
	}

	public void setCallbackSuccess(String callbackSuccess) {
		this.callbackSuccess = callbackSuccess;
	}

	public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
	}

	public CheckoutBody(int amount, String description, String userName, String callbackFail, String callbackSuccess,
			String notificationUrl) {
		this.amount = amount;
		this.description = description;
		this.userName = userName;
		this.callbackFail = callbackFail;
		this.callbackSuccess = callbackSuccess;
		this.notificationUrl = notificationUrl;
	}

	public CheckoutBody() {
	}

}
