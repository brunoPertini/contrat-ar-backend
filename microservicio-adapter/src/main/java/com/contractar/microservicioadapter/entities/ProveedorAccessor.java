package com.contractar.microservicioadapter.entities;

import com.contractar.microservicioadapter.enums.Proveedor;

public interface ProveedorAccessor extends UsuarioAccesor{
	public String getDni();

	public SuscripcionAccesor getSuscripcion();

	public Proveedor getProveedorType();

	public String getFotoPerfilUrl();
	
	public boolean hasWhatsapp();
}
