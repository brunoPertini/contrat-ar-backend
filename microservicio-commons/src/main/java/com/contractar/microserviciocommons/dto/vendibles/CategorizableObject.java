package com.contractar.microserviciocommons.dto.vendibles;

import com.contractar.microserviciovendible.models.VendibleCategory;

public interface CategorizableObject {
	public VendibleCategory getCategory();
	public void setCategory(VendibleCategory category);
}
