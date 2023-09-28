package com.contractar.microserviciocommons.dto.vendibles;

import java.util.Objects;

import com.contractar.microserviciovendible.models.VendibleCategory;

public class VendibleCategoryDTO implements Comparable<VendibleCategoryDTO>{
	private Long id;
	private String name;
	private Long parentId;

	public VendibleCategoryDTO() {
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
		if (this.getId() != null) {
			return this.getId() == category.getId();
		}
		return this.getName() == category.getName();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId(), this.getName());
	}
	
	/**
	 * If the current category is c's parent, then its lower than c.
	 */
	@Override
	public int compareTo(VendibleCategoryDTO c) {
		if (this.getId() == c.getId()) {
			return 0;
		}
		
		if (this.getId() == c.getParentId()) {
			return -1;
		}
		
		return 1;
	}

}
