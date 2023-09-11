package com.contractar.microserviciovendible.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.repository.ServicioRepository;

@Service
public class ServicioService {
	@Autowired
	private ServicioRepository servicioRepository;

	public VendiblesResponseDTO findByNombreAsc(String nombre) {
		VendiblesResponseDTO response = new VendiblesResponseDTO();
		this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream().forEach(servicio -> {
			Set<SimplifiedProveedorVendibleDTO> proveedoresVendibles = servicio.getProveedoresVendibles().stream()
					.map(proveedorVendible -> {
						Proveedor proveedor = proveedorVendible.getProveedor();
						SimplifiedProveedorVendibleDTO proveedorVendibleDTO = new SimplifiedProveedorVendibleDTO(
								servicio.getNombre(), proveedorVendible.getDescripcion(), proveedorVendible.getPrecio(),
								proveedorVendible.getImagenUrl(), proveedorVendible.getStock(), proveedor.getId());
						response.getProveedores().add(new ProveedorDTO(proveedor));
						return proveedorVendibleDTO;
					}).collect(Collectors.toSet());
			response.getVendibles().addAll(proveedoresVendibles);
		});

		return response;
	}
}
