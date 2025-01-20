package com.contractar.microserviciopayment.providers.uala;

import com.contractar.microserviciopayment.models.enums.UalaPaymentStateValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebhookBody {
	private String uuid;
	
	@JsonProperty("external_reference")
	private String externalReference;
	private UalaPaymentStateValue status;
	
	private String created_date;
	
	@JsonProperty("api_version")
	private String apiVersion;
	
	
	public WebhookBody() {
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getExternalReference() {
		return externalReference;
	}
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}
	public UalaPaymentStateValue getStatus() {
		return status;
	}
	public void setStatus(UalaPaymentStateValue status) {
		this.status = status;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	public String getApiVersion() {
		return apiVersion;
	}
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	
}
