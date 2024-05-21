package com.contractar.microservicioadapter.entities;

import com.contractar.microservicioadapter.enums.PlanAccesor;
import com.contractar.microservicioadapter.enums.Proveedor;

public interface ProveedorAccessor extends UsuarioAccesor{
	public String getDni();

	public PlanAccesor getPlan();

	public Proveedor getProveedorType();

	public String getFotoPerfilUrl();
}
