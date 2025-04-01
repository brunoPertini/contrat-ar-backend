package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotBlank;

public class PlanChangeConfirmation extends MailInfo {

	@NotBlank
	private String userName;

	@NotBlank
	private String destinyPlan;

	public PlanChangeConfirmation() {
	}

	public PlanChangeConfirmation(@NotBlank String toAddress, @NotBlank String userName,
			@NotBlank String destinyPlan) {
		super(toAddress);
		this.userName = userName;
		this.destinyPlan = destinyPlan;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDestinyPlan() {
		return destinyPlan;
	}

	public void setDestinyPlan(String destinyPlan) {
		this.destinyPlan = destinyPlan;
	}

}
