package com.contractar.microserviciocommons.dto.proveedorvendible;

import com.contractar.microservicioadapter.enums.PriceTypeInterface;

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

	public int getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}

	public PriceTypeInterface getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceTypeInterface priceType) {
		this.priceType = priceType;
	}

	public boolean isOffersDelivery() {
		return offersDelivery;
	}

	public void setOffersDelivery(boolean offersDelivery) {
		this.offersDelivery = offersDelivery;
	}

	public boolean isOffersInCustomAddress() {
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

	public int getMinStock() {
		return minStock;
	}

	public void setMinStock(int minStock) {
		this.minStock = minStock;
	}

	public int getMaxStock() {
		return maxStock;
	}

	public void setMaxStock(int maxStock) {
		this.maxStock = maxStock;
	}
	
	
}
