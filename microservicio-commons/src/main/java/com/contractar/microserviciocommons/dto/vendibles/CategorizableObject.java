package com.contractar.microserviciocommons.dto.vendibles;

import com.contractar.microservicioadapter.entities.VendibleCategoryAccesor;

public interface CategorizableObject {
	public VendibleCategoryAccesor getCategory();
	public void setCategory(VendibleCategoryAccesor category);
}
