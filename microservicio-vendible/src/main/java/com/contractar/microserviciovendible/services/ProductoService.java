package com.contractar.microserviciovendible.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.dto.ProductoDTO;
import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.ProveedorVendibleDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.repository.ProductoRepository;

@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	public List<ProductoDTO> findByNombreAsc(String nombre) {
		try {
			return this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream()
					.map(producto -> {
						ProductoDTO productoDTO = new ProductoDTO(producto.getNombre());
						Set<ProveedorVendibleDTO> proveedoresVendibles = producto.getProveedoresVendibles().stream()
								.map(proveedorVendible -> {
									Proveedor proveedor = proveedorVendible.getProveedor();
									ProveedorVendibleDTO proveedorVendibleDTO = new ProveedorVendibleDTO(
											producto.getNombre(), proveedorVendible.getDescripcion(),
											proveedorVendible.getPrecio(), proveedorVendible.getImagenUrl(),
											proveedorVendible.getStock(), new ProveedorDTO(proveedor));
									return proveedorVendibleDTO;
								}).collect(Collectors.toSet());

						productoDTO.setProveedores(proveedoresVendibles);
						return productoDTO;
					}).collect(Collectors.toList());
		} catch (Exception e) {
			throw e;
		}
	}

}
