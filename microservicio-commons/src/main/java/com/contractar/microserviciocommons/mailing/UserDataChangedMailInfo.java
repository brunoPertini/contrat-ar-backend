package com.contractar.microserviciocommons.mailing;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class UserDataChangedMailInfo extends MailInfo {
	@NotEmpty
	private List<String> fieldsList;
	
	
	public UserDataChangedMailInfo(String toAddress, List<String> fieldsList) {
		super(toAddress);
		this.fieldsList = fieldsList;
	}

	public List<String> getFieldsList() {
		return fieldsList;
	}

	public void setFieldsList(List<String> fieldsList) {
		this.fieldsList = fieldsList;
	}
	
	
}
