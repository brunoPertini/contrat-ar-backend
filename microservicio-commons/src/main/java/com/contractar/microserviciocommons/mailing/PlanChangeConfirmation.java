package com.contractar.microserviciocommons.mailing;

import jakarta.validation.constraints.NotBlank;

public class PlanChangeConfirmation extends MailInfo {

	@NotBlank
	private String userName;

	@NotBlank
	private String sourcePlan;

	@NotBlank
	private String destinyPlan;

	public PlanChangeConfirmation() {
	}

	public PlanChangeConfirmation(@NotBlank String toAddress, @NotBlank String userName, @NotBlank String sourcePlan,
			@NotBlank String destinyPlan) {
		super(toAddress);
		this.userName = userName;
		this.sourcePlan = sourcePlan;
		this.destinyPlan = destinyPlan;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSourcePlan() {
		return sourcePlan;
	}

	public void setSourcePlan(String sourcePlan) {
		this.sourcePlan = sourcePlan;
	}

	public String getDestinyPlan() {
		return destinyPlan;
	}

	public void setDestinyPlan(String destinyPlan) {
		this.destinyPlan = destinyPlan;
	}

}
