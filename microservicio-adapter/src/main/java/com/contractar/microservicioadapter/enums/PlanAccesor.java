package com.contractar.microservicioadapter.enums;

public interface PlanAccesor {
	public void setId(Long id);

	public Long getId();

	public void setDescripcion(String descripcion);

	public String getDescripcion();

	public void setType(PlanType type);

	public PlanType getType();

	public void setPrice(int price);

	public int getPrice();

}
