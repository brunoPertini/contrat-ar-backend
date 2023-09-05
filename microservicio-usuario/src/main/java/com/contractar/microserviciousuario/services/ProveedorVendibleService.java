package com.contractar.microserviciousuario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;
import com.contractar.microserviciousuario.repository.ProveedorVendibleRepository;
import com.contractar.microserviciovendible.models.Vendible;

@Service
public class ProveedorVendibleService {
	@Autowired
	private ProveedorVendibleRepository repository;

	public ProveedorVendible bindVendibleToProveedor(Vendible vendible, Proveedor proveedor,
			ProveedorVendible proveedorVendible) {
		proveedorVendible.setProveedor(proveedor);
		proveedorVendible.setVendible(vendible);
		proveedorVendible.setId(new ProveedorVendibleId(proveedor.getId(), vendible.getId()));
		return repository.save(proveedorVendible);
	}
}
