package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotNull;

public class AdminChangeRequestInfo extends MailInfo{
	@NotNull
	private Long changeRequestId;
	
	@NotNull
	private String requestEntity;

	public AdminChangeRequestInfo(String toAddress, Long changeRequestId, String requestEntity) {
		super(toAddress);
		this.changeRequestId = changeRequestId;
		this.requestEntity = requestEntity;
	}

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public String getRequestEntity() {
		return requestEntity;
	}

	public void setRequestEntity(String requestEntity) {
		this.requestEntity = requestEntity;
	}

}
