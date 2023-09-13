package com.contractar.microserviciocommons.vendibles;

import java.util.Set;
import java.util.stream.Collectors;

import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.models.Vendible;

public final class VendibleHelper {

	public static Set<SimplifiedProveedorVendibleDTO> getProveedoresVendibles(VendiblesResponseDTO response,
			Vendible vendible) {
		return vendible.getProveedoresVendibles().stream().map(proveedorVendible -> {
			Proveedor proveedor = proveedorVendible.getProveedor();
			SimplifiedProveedorVendibleDTO proveedorVendibleDTO = new SimplifiedProveedorVendibleDTO(
					vendible.getNombre(), proveedorVendible.getDescripcion(), proveedorVendible.getPrecio(),
					proveedorVendible.getImagenUrl(), proveedorVendible.getStock(), proveedor.getId());
			response.getProveedores().add(new ProveedorDTO(proveedor));
			return proveedorVendibleDTO;
		}).collect(Collectors.toSet());
	}

}
