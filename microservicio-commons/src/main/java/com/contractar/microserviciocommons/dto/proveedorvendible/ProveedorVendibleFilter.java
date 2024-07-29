package com.contractar.microserviciocommons.dto.proveedorvendible;

import com.contractar.microservicioadapter.enums.PriceTypeInterface;
import com.contractar.microserviciocommons.constants.PriceType.PriceTypeValue;

public class ProveedorVendibleFilter {
	private String proveedorName;
	
	private String proveedorSurname;
	
	private Integer minPrice;
	
	private Integer maxPrice;
	
	private PriceTypeInterface priceType;
	
	private Boolean offersDelivery;
	
	private Boolean offersInCustomAddress;
	
	private String categoryName;
	
	private Integer minStock;
	
	private Integer maxStock;
	
	public ProveedorVendibleFilter () {}
	

	public ProveedorVendibleFilter(String proveedorName, String proveedorSurname, int minPrice, int maxPrice, PriceTypeInterface priceType,
			boolean offersDelivery, boolean offersInCustomAddress, String categoryName, int minStock, int maxStock) {
		this.proveedorName = proveedorName;
		this.proveedorSurname = proveedorSurname;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.priceType = priceType;
		this.offersDelivery = offersDelivery;
		this.offersInCustomAddress = offersInCustomAddress;
		this.categoryName = categoryName;
		this.minStock = minStock;
		this.maxStock = maxStock;
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

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public Integer getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
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

	public void setOffersDelivery(boolean offersDelivery) {
		this.offersDelivery = offersDelivery;
	}

	public Boolean isOffersInCustomAddress() {
		return offersInCustomAddress;
	}

	public void setOffersInCustomAddress(boolean offersInCustomAddress) {
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

	public void setMinStock(int minStock) {
		this.minStock = minStock;
	}

	public Integer getMaxStock() {
		return maxStock;
	}

	public void setMaxStock(int maxStock) {
		this.maxStock = maxStock;
	}
	
	
}
