package com.contractar.microserviciocommons.dto.usuario;

import org.locationtech.jts.geom.Point;

import com.contractar.microserviciocommons.plans.PlanType;

public class ProveedorInfoUpdateDTO extends UsuarioCommonInfoUpdateDTO {
	private String fotoPerfilUrl;
	private PlanType plan;
	
	public ProveedorInfoUpdateDTO(Point location, String phone, String fotoPerfilUrl, PlanType plan) {
		super(location, phone);
		this.plan = plan;
		this.fotoPerfilUrl = fotoPerfilUrl;
	}
	
	public String getFotoPerfilUrl() {
		return fotoPerfilUrl;
	}

	public void setFotoPerfilUrl(String fotoPerfilUrl) {
		this.fotoPerfilUrl = fotoPerfilUrl;
	}

	public PlanType getPlan() {
		return plan;
	}

	public void setPlan(PlanType plan) {
		this.plan = plan;
	}

}
