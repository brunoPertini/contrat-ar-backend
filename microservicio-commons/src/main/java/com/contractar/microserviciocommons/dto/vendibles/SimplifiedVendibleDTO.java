package com.contractar.microserviciocommons.dto.vendibles;

import java.util.List;
import java.util.Map;

import com.contractar.microservicioadapter.entities.ProveedorVendibleAccesor;
import com.contractar.microservicioadapter.enums.PostState;
import com.contractar.microserviciocommons.dto.proveedorvendible.AbstractProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.locationtech.jts.geom.Point;

public class SimplifiedVendibleDTO extends AbstractProveedorVendibleDTO {

	private Point location;
	private PostState state;

	private List<String> categoryNames;

	public SimplifiedVendibleDTO(Long vendibleId, List<String> categoryNames, String vendibleNombre, String descripcion,
			int precio, String imagenUrl, int stock, Map<String, CategoryHierarchy> categorias) {
		super(vendibleNombre, descripcion, precio, imagenUrl, stock);
		this.categoryNames = categoryNames;
	}

	public SimplifiedVendibleDTO(ProveedorVendibleAccesor entity, List<String> categoryNames) {
		super(entity.getVendible().getId(), entity.getVendible().getNombre(), entity.getDescripcion(),
				entity.getPrecio(), entity.getTipoPrecio(), entity.getOffersDelivery(),
				entity.getOffersInCustomAddress(), entity.getImagenUrl(), entity.getStock(),
				entity.getCategory().getId());
		this.location = entity.getLocation();
		this.state = entity.getState();
		this.categoryNames = categoryNames;
		this.setVendibleType(entity.getVendibleType());
	}

	public SimplifiedVendibleDTO() {
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

	public PostState getState() {
		return state;
	}

	public void setState(PostState state) {
		this.state = state;
	}

	@Override
	public Long getVendibleCategoryId() {
		return super.getVendibleCategoryId();
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Override
	public Long getPlanId() {
		return null;
	}
}
