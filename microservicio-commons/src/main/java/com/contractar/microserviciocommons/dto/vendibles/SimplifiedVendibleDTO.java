package com.contractar.microserviciocommons.dto.vendibles;

import java.util.List;
import java.util.Map;

import com.contractar.microserviciocommons.dto.proveedorvendible.AbstractProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Point;

public class SimplifiedVendibleDTO extends AbstractProveedorVendibleDTO {

	private Long vendibleId;
	private Point location;

	private List<String> categoryNames;

	public SimplifiedVendibleDTO(Long vendibleId, List<String> categoryNames, String vendibleNombre, String descripcion,
			int precio, String imagenUrl, int stock, Map<String, CategoryHierarchy> categorias) {
		super(vendibleNombre, descripcion, precio, imagenUrl, stock);
		this.vendibleId = vendibleId;
		this.categoryNames = categoryNames;
	}

	public SimplifiedVendibleDTO() {}

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
	
	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	@JsonIgnore
	@Override
	public Long getVendibleCategoryId() {
		return super.getVendibleCategoryId();
	}

	@Override
	public Long getPlanId() {
		return null;
	}
}
