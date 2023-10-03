package com.contractar.microserviciocommons.dto.vendibles;

import java.util.Objects;

public class VendibleCategoryDTO{
	private Long id;
	private String name;
	private Long parentId;

	public VendibleCategoryDTO() {
		this.id = null;
		this.name = "";
		this.parentId = null;
	}

	public VendibleCategoryDTO(Long id, String name, Long parentId) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;
		
		VendibleCategoryDTO category = (VendibleCategoryDTO) obj;
		
		boolean canCompareParents = this.getParentId() != null && category.getParentId() != null;
		boolean areMainFieldsEqual = this.getId().equals(category.getId()) && this.getName().equals(category.getName());
		
		return  canCompareParents ? canCompareParents && areMainFieldsEqual : areMainFieldsEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId(), this.getName(), this.getParentId());
	}
}
