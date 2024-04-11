package com.contractar.microservicioadapter.entities;

public interface VendibleCategoryAccesor {
	public Long getId();

	public String getName();

	public VendibleCategoryAccesor getParent();
}
