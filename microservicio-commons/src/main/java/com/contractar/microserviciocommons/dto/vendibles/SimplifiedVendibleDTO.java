package com.contractar.microserviciocommons.dto.vendibles;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SimplifiedVendibleDTO extends AbstractProveedorVendibleDTO {

	private Long vendibleId;
	private List<String> categoryNames;

	public SimplifiedVendibleDTO(Long vendibleId, List<String> categoryNames, String vendibleNombre, String descripcion,
			int precio, String imagenUrl, int stock) {
		super(vendibleNombre, descripcion, precio, imagenUrl, stock);
		this.vendibleId = vendibleId;
		this.categoryNames = categoryNames;
	}

	public SimplifiedVendibleDTO() {
		// TODO Auto-generated constructor stub
	}

	public Long getVendibleId() {
		return vendibleId;
	}

	public void setVendibleId(Long vendibleId) {
		this.vendibleId = vendibleId;
	}

	public List<String> getCategoryNames() {
		return categoryNames;
	}

	public void setCategoryNames(List<String> categoryNames) {
		this.categoryNames = categoryNames;
	}

	@JsonIgnore
	@Override
	public Long getVendibleCategoryId() {
		return super.getVendibleCategoryId();
	}

}
