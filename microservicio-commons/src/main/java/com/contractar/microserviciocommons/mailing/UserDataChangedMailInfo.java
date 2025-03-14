package com.contractar.microserviciocommons.mailing;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UserDataChangedMailInfo extends MailInfo {
	@NotBlank
	private String userName;

	@NotEmpty
	private List<String> fieldsList;

	@NotNull
	private Long userId;

	@NotBlank
	private String roleName;

	public UserDataChangedMailInfo(String toAddress, List<String> fieldsList, String userName, Long userId,
			String roleName) {
		super(toAddress);
		this.fieldsList = fieldsList;
		this.userName = userName;
		this.userId = userId;
		this.roleName = roleName;
	}

	public List<String> getFieldsList() {
		return fieldsList;
	}

	public void setFieldsList(List<String> fieldsList) {
		this.fieldsList = fieldsList;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
