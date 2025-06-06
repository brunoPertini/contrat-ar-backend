package com.contractar.microserviciocommons.dto.proveedorvendible;

import com.contractar.microservicioadapter.enums.PostState;
import com.contractar.microservicioadapter.enums.PriceTypeInterface;
import com.contractar.microserviciocommons.constants.PriceType.PriceTypeValue;

/**
 * Filters that can be used to search posts from frontend. They may be used by any user role. 
 */
public class ProveedorVendibleFilter {
	private String proveedorName;
	
	private String vendibleNombre;
	
	private String proveedorSurname;
	
	private Integer minPrice;
	
	private Integer maxPrice;
	
	private PriceTypeInterface priceType;
	
	private Boolean offersDelivery;
	
	private Boolean offersInCustomAddress;
	
	private String categoryName;
	
	private Integer minStock;
	
	private Integer maxStock;
	
	private PostState state;

	public ProveedorVendibleFilter () {}
	
	private ProveedorVendibleFilter(Integer minPrice, Integer maxPrice, PriceTypeInterface priceType,
			Boolean offersDelivery, Boolean offersInCustomAddress, String categoryName, Integer minStock, Integer maxStock) {
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.priceType = priceType;
		this.offersDelivery = offersDelivery;
		this.offersInCustomAddress = offersInCustomAddress;
		this.categoryName = categoryName;
		this.minStock = minStock;
		this.maxStock = maxStock;
	}
	

	public ProveedorVendibleFilter(String proveedorName, String proveedorSurname, Integer minPrice, Integer maxPrice, PriceTypeInterface priceType,
			Boolean offersDelivery, Boolean offersInCustomAddress, String categoryName, Integer minStock, Integer maxStock) {
		this(minPrice, maxPrice, priceType, offersDelivery, offersInCustomAddress, categoryName, minStock, maxStock);
		this.proveedorName = proveedorName;
		this.proveedorSurname = proveedorSurname;
	}
	
	public ProveedorVendibleFilter(String vendibleNombre, Integer minPrice, Integer maxPrice, PriceTypeInterface priceType,
			Boolean offersDelivery, Boolean offersInCustomAddress, String categoryName, Integer minStock, Integer maxStock) {
		this(minPrice, maxPrice, priceType, offersDelivery, offersInCustomAddress, categoryName, minStock, maxStock);
		this.vendibleNombre = vendibleNombre;
	}

	public String getProveedorName() {
		return proveedorName;
	}

	public void setProveedorName(String proveedorName) {
		this.proveedorName = proveedorName;
	}
	
	public String getProveedorSurname() {
		return proveedorSurname;
	}

	public void setProveedorSurname(String proveedorSurname) {
		this.proveedorSurname = proveedorSurname;
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

	public PriceTypeInterface getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceTypeValue priceType) {
		this.priceType = priceType;
	}

	public Boolean isOffersDelivery() {
		return offersDelivery;
	}

	public void setOffersDelivery(Boolean offersDelivery) {
		this.offersDelivery = offersDelivery;
	}

	public Boolean isOffersInCustomAddress() {
		return offersInCustomAddress;
	}

	public void setOffersInCustomAddress(Boolean offersInCustomAddress) {
		this.offersInCustomAddress = offersInCustomAddress;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Integer getMinStock() {
		return minStock;
	}

	public void setMinStock(Integer minStock) {
		this.minStock = minStock;
	}

	public Integer getMaxStock() {
		return maxStock;
	}

	public void setMaxStock(Integer maxStock) {
		this.maxStock = maxStock;
	}
	
	public String getVendibleNombre() {
		return vendibleNombre;
	}


	public void setVendibleNombre(String vendibleNombre) {
		this.vendibleNombre = vendibleNombre;
	}
	
	public PostState getState() {
		return state;
	}

	public void setState(PostState state) {
		this.state = state;
	}

}
