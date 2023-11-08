package com.contractar.microserviciousuario.services;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendibleUpdateDTO;
import com.contractar.microserviciocommons.dto.vendibles.SimplifiedVendibleDTO;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleUpdateException;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
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
	
	@Value("${microservicio-vendible.url}")
	private String SERVICIO_USUARIO_URL;

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
	public List<SimplifiedVendibleDTO> getProveedorVendiblesInfo(Long proveedorId) {
		List<Object[]> results = proveedorVendibleCustomRepository.getProveedorVendiblesInfo(proveedorId);
		
		List<SimplifiedVendibleDTO> simplifiedVendibleDTOs = new ArrayList<SimplifiedVendibleDTO>();

		for (Object[] result : results) {
			SimplifiedVendibleDTO simplifiedVendibleDTO = new SimplifiedVendibleDTO();
			
			String categoryName = (String) result[4];
			
			String getVendibleHierachyStringUrl = (SERVICIO_USUARIO_URL + VendiblesControllersUrls.GET_CATEGORY_HIERACHY)
					.replace("{categoryName}", UriUtils.encodePathSegment(categoryName, "UTF-8"));
						
			List<String> categoryNames = httpClient.getForObject(getVendibleHierachyStringUrl, List.class);
			simplifiedVendibleDTO.setVendibleId((Long) result[0]);
			simplifiedVendibleDTO.setVendibleNombre((String) result[1]);
			simplifiedVendibleDTO.setDescripcion((String) result[2]);
			simplifiedVendibleDTO.setImagenUrl((String) result[3]);
			simplifiedVendibleDTO.setCategoryNames(categoryNames);

			simplifiedVendibleDTOs.add(simplifiedVendibleDTO);
		}

		return simplifiedVendibleDTOs;

	}
	
}
