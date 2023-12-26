package com.contractar.microserviciousuario.services;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleUpdateDTO;
import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.SimplifiedVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleEntityDTO;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleUpdateException;
import com.contractar.microserviciocommons.infra.SecurityHelper;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;
import com.contractar.microserviciousuario.repository.ProveedorVendibleRepository;
import com.contractar.microserviciousuario.repository.customrepositories.ProveedorVendibleCustomRepositoryImpl;
import com.contractar.microserviciovendible.models.Vendible;

@Service
public class ProveedorVendibleService {
	@Autowired
	private ProveedorVendibleRepository repository;

	@Autowired
	private ProveedorVendibleCustomRepositoryImpl proveedorVendibleCustomRepository;

	@Autowired
	private RestTemplate httpClient;
	
	@Autowired
	private SecurityHelper securityHelper;

	@Value("${microservicio-vendible.url}")
	private String SERVICIO_VENDIBLE_URL;

	public ProveedorVendible bindVendibleToProveedor(Vendible vendible, Proveedor proveedor,
			ProveedorVendible proveedorVendible) {
		proveedorVendible.setProveedor(proveedor);
		proveedorVendible.setVendible(vendible);
		proveedorVendible.setId(new ProveedorVendibleId(proveedor.getId(), vendible.getId()));
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
		List<Object[]> results = proveedorVendibleCustomRepository.getProveedorVendiblesInfo(proveedorId);

		ProveedorVendiblesResponseDTO response = new ProveedorVendiblesResponseDTO();

		for (Object[] result : results) {
			SimplifiedVendibleDTO simplifiedVendibleDTO = new SimplifiedVendibleDTO();

			Long vendibleId = (Long) result[0];

			String imagenUrl = (String) result[3];

			String categoryName = (String) result[4];

			int precio = (int) result[5];

			int stock = (int) result[6];

			String getVendibleHierachyStringUrl = (SERVICIO_VENDIBLE_URL
					+ VendiblesControllersUrls.GET_CATEGORY_HIERACHY)
					.replace("{categoryName}", UriUtils.encodePathSegment(categoryName, "UTF-8"));

			String getVendibleStringUrl = (SERVICIO_VENDIBLE_URL + VendiblesControllersUrls.GET_VENDIBLE_BY_ID)
					.replace("{vendibleId}", UriUtils.encodePathSegment(vendibleId.toString(), "UTF-8"));

			List<String> categoryNames = httpClient.getForObject(getVendibleHierachyStringUrl, List.class);
			
			VendibleEntityDTO vendible = httpClient.getForObject(getVendibleStringUrl, VendibleEntityDTO.class);

			simplifiedVendibleDTO.setVendibleId(vendibleId);
			simplifiedVendibleDTO.setVendibleNombre(vendible.getNombre());
			simplifiedVendibleDTO.setDescripcion((String) result[2]);
			simplifiedVendibleDTO.setImagenUrl(StringUtils.isEmpty(imagenUrl) ? null : imagenUrl);
			simplifiedVendibleDTO.setCategoryNames(categoryNames);
			simplifiedVendibleDTO.setPrecio(precio);
			simplifiedVendibleDTO.setStock(stock);
			
			VendibleHelper.addCategoriasToResponse(vendible, response);

			response.getVendibles().add(simplifiedVendibleDTO);
		}

		return response;

	}

}
