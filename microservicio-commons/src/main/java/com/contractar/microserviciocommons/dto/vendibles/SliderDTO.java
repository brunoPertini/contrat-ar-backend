package com.contractar.microserviciocommons.dto.vendibles;

/**
 * The sliders at the frontend need this info to know which the boundaries are.
 * Used to filter proveedores posts.
 */
public class SliderDTO {
	private Double minDistance;
	private Double maxDistance;
	private Integer minPrice;
	private Integer maxPrice;
	
	public SliderDTO() {}
	
	
	public SliderDTO(Double minDistance, Double maxDistance, Integer minPrice, Integer maxPrice) {
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
	}

	public Double getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(Double minDistance) {
		this.minDistance = minDistance;
	}

	public Double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Double maxDistance) {
		this.maxDistance = maxDistance;
	}
	public Integer getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public Integer getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}

}
