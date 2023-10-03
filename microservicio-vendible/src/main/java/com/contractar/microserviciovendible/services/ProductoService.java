package com.contractar.microserviciovendible.services;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.dto.vendibles.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleCategoryDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciovendible.repository.ProductoRepository;

@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	public VendiblesResponseDTO findByNombreAsc(String nombre) {
		VendiblesResponseDTO response = new VendiblesResponseDTO();
		this.productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream().forEach(producto -> {
			Set<SimplifiedProveedorVendibleDTO> proveedoresVendibles = VendibleHelper.getProveedoresVendibles(response,
					producto);
			if (proveedoresVendibles.size() > 0) {
				response.getVendibles().put(producto.getNombre(), proveedoresVendibles);
			}

			VendibleHelper.addCategoriasToResponse(producto, response);
		});

		Comparator<VendibleCategoryDTO> comparator = (c1, c2) -> {
			boolean someHasNullParentId = c1.getParentId() == null || c2.getParentId() == null;
			
			if (!someHasNullParentId) {
				if (c1.getId().equals(c2.getParentId())) {
					return -1;
				} else if (c2.getId().equals(c1.getParentId())){
					return 1;
				} else {
					return 1;
				}
			} else {
				boolean bothHaveNullParentId = c1.getParentId() == null && c2.getParentId() == null;
				
				if (bothHaveNullParentId) {
					return 1;
				} else {
					if (c1.getParentId() == null && c2.getParentId() != null) {
						return -1;
					} else {
						return 1;
					}
				}
				
			}
		};

		Set<VendibleCategoryDTO> orderedCategories = new TreeSet<VendibleCategoryDTO>(comparator);
		response.getCategorias().forEach(c -> {
			Set<String> oldNames = orderedCategories.stream().map(cat -> cat.getName()).collect(Collectors.toSet());
			System.out.println("Estado actual: " + oldNames);
			orderedCategories.add(c);
			Set<String> newNames = orderedCategories.stream().map(cat -> cat.getName()).collect(Collectors.toSet());
			System.out.println("Estado nuevo: " + newNames);
			
		});

		response.getCategorias().forEach(c -> System.out.println(c.getName()));
		response.setCategorias(orderedCategories);

		return response;
	}

}
