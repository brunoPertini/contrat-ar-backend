package com.contractar.microservicioadapter.entities;

import com.contractar.microservicioadapter.enums.Plan;
import com.contractar.microservicioadapter.enums.Proveedor;

public interface ProveedorAccessor extends UsuarioAccesor{
	public String getDni();

	public Plan getPlan();

	public Proveedor getProveedorType();

	public String getFotoPerfilUrl();

	public String getPhone();	
}
