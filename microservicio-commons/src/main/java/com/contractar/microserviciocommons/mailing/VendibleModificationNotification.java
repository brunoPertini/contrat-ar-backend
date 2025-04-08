package com.contractar.microserviciocommons.mailing;

public class VendibleModificationNotification extends MailNotificationResultBody {
	private String vendibleName;

	public VendibleModificationNotification(String toAddress, boolean result, String userName, String vendibleName) {
		super(toAddress, result, userName);
		this.vendibleName = vendibleName;
	}

	public String getVendibleName() {
		return vendibleName;
	}

	public void setVendibleName(String vendibleName) {
		this.vendibleName = vendibleName;
	}

}
