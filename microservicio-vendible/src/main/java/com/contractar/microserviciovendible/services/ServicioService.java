package com.contractar.microserviciovendible.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.ProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.ServicioDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.repository.ServicioRepository;

@Service
public class ServicioService {
	@Autowired
	private ServicioRepository servicioRepository;

	public List<ServicioDTO> findByNombreAsc(String nombre) {
		try {
			return this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream()
					.map(servicio -> {
						// TODO: extraer a helper para modularizar
						Set<ProveedorVendibleDTO> proveedoresVendibles = servicio.getProveedoresVendibles()
								.stream()
								.map(proveedorVendible -> {
									Proveedor proveedor = proveedorVendible.getProveedor();
									ProveedorVendibleDTO proveedorVendibleDTO = new ProveedorVendibleDTO(servicio.getNombre(),
											proveedorVendible.getDescripcion(), 
											proveedorVendible.getPrecio(),
											proveedorVendible.getImagenUrl(),
											proveedorVendible.getStock(),
											new ProveedorDTO(proveedor));
											return proveedorVendibleDTO;
								}).collect(Collectors.toSet());
						return new ServicioDTO(servicio.getNombre(), proveedoresVendibles);
					}).collect(Collectors.toList());
		} catch (Exception e) {
			throw e;
		}
	}
}
