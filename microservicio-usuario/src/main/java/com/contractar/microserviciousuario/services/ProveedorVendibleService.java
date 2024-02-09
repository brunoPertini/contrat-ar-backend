package com.contractar.microserviciousuario.services;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleUpdateDTO;
import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.SimplifiedVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleProveedoresDTO;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleUpdateException;
import com.contractar.microserviciocommons.infra.SecurityHelper;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciousuario.dtos.DistanceProveedorDTO;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;
import com.contractar.microserviciousuario.repository.ProveedorVendibleRepository;
import com.contractar.microserviciovendible.models.Vendible;

@Service
public class ProveedorVendibleService {
	@Autowired
	private ProveedorVendibleRepository repository;

	@Autowired
	private RestTemplate httpClient;
	
	@Autowired
	private SecurityHelper securityHelper;

	@Value("${microservicio-vendible.url}")
	private String SERVICIO_VENDIBLE_URL;

	public ProveedorVendible bindVendibleToProveedor(Vendible vendible, Proveedor proveedor,
			ProveedorVendible proveedorVendible) throws VendibleAlreadyBindedException {
		ProveedorVendibleId id = new ProveedorVendibleId(proveedor.getId(), vendible.getId());
		if (repository.findById(id).isPresent())  {
			throw new VendibleAlreadyBindedException();
		}
		proveedorVendible.setProveedor(proveedor);
		proveedorVendible.setVendible(vendible);
		proveedorVendible.setId(id);
		return repository.save(proveedorVendible);
	}

	public void unBindVendible(Long vendibleId, Long proveedorId) throws VendibleNotFoundException {
		try {
			ProveedorVendibleId id = new ProveedorVendibleId(proveedorId, vendibleId);
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException ex) {
			throw new VendibleNotFoundException();
		}
	}

	public void updateVendible(Long vendibleId, Long proveedorId, ProveedorVendibleUpdateDTO newData)
			throws VendibleNotFoundException, VendibleUpdateException {
		if (!securityHelper.isResponseContentTypeValid(newData.getImagenUrl(), "image")) {
			throw new VendibleUpdateException();
		}
		
		ProveedorVendibleId id = new ProveedorVendibleId(proveedorId, vendibleId);
		ProveedorVendible vendible = this.repository.findById(id).orElseThrow(() -> new VendibleNotFoundException());

		String dtoFullClassName = ProveedorVendibleUpdateDTO.class.getPackage().getName()
				+ ".ProveedorVendibleUpdateDTO";
		String entityFullClassName = ProveedorVendible.class.getPackage().getName() + ".ProveedorVendible";

		try {
			ReflectionHelper.applySetterFromExistingFields(newData, vendible, dtoFullClassName, entityFullClassName);
			repository.save(vendible);
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			throw new VendibleUpdateException();
		}
	}

	@SuppressWarnings("unchecked")
	public ProveedorVendiblesResponseDTO getProveedorVendiblesInfo(Long proveedorId) {
		List<ProveedorVendible> results = repository.getProveedorVendibleInfo(proveedorId);

		ProveedorVendiblesResponseDTO response = new ProveedorVendiblesResponseDTO();

		for (ProveedorVendible pv: results) {
			SimplifiedVendibleDTO simplifiedVendibleDTO = new SimplifiedVendibleDTO();

			String getVendibleHierachyStringUrl = (SERVICIO_VENDIBLE_URL
					+ VendiblesControllersUrls.GET_CATEGORY_HIERACHY);

			List<String> categoryNames = pv.getCategory() != null ? 
					httpClient.postForObject(getVendibleHierachyStringUrl, pv.getCategory(), List.class)
					: List.of();

			simplifiedVendibleDTO.setVendibleId(pv.getVendible().getId());
			simplifiedVendibleDTO.setVendibleNombre(pv.getVendible().getNombre());
			simplifiedVendibleDTO.setDescripcion(pv.getDescripcion());
			simplifiedVendibleDTO.setImagenUrl(StringUtils.isEmpty(pv.getImagenUrl()) ? null : pv.getImagenUrl());
			simplifiedVendibleDTO.setCategoryNames(categoryNames);
			simplifiedVendibleDTO.setPrecio(pv.getPrecio());
			simplifiedVendibleDTO.setStock(pv.getStock());
			
			VendibleHelper.addCategoriasToResponse(pv, response);

			response.getVendibles().add(simplifiedVendibleDTO);
		}

		return response;

	}
	
	public VendibleProveedoresDTO getProveedoreVendiblesInfoForVendible(Long vendibleId) {
		VendibleProveedoresDTO response = new VendibleProveedoresDTO();
		
		List<ProveedorVendible> results = repository.getProveedoreVendiblesInfoForVendible(vendibleId);
		
		results.forEach(proveedorVendible -> {
			response.getVendibles().add(new DistanceProveedorDTO(proveedorVendible.getVendible().getNombre(),
					proveedorVendible.getDescripcion(), proveedorVendible.getPrecio(), proveedorVendible.getImagenUrl(),
					proveedorVendible.getStock(), proveedorVendible.getProveedor().getId()));
			
			response.getProveedores().add(new ProveedorDTO(proveedorVendible.getProveedor()));
		});
		
		return response;
		
	}

}
