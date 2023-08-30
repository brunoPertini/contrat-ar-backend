package com.contractar.microserviciovendible.services;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.dto.VendibleDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleAlreadyExistsException;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciovendible.repository.ProductoRepository;
import com.contractar.microserviciovendible.repository.ServicioRepository;
import com.contractar.microserviciovendible.repository.VendibleRepository;

import jakarta.transaction.Transactional;

@Service
public class VendibleService {
	@Autowired
	private VendibleRepository vendibleRepository;

	@Autowired
	private ServicioRepository servicioRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Value("${microservicio-usuario.url}")
	private String microServicioUsuarioUrl;

	@Autowired
	private RestTemplate restTemplate;

	public Vendible save(Vendible vendible, String vendibleType, Long proveedorId) throws Exception {
		try {
			ProveedorType proveedorType = vendibleType.equals(VendibleType.SERVICIO.toString())
					? ProveedorType.SERVICIOS
					: ProveedorType.PRODUCTOS;
			
			  UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(microServicioUsuarioUrl)
		                .path(UsersControllerUrls.GET_PROVEEDOR)
		                .queryParam("id", proveedorId)
		                .queryParam("proveedorType", proveedorType);

		    String usuarioExistsUrl = builder.toUriString();


			ResponseEntity<Void> getUsuarioResponse = restTemplate.getForEntity(usuarioExistsUrl, Void.class);

			if (getUsuarioResponse.getStatusCode().is2xxSuccessful()) {
				Vendible addedVendible = vendibleType.equals(VendibleType.SERVICIO.name())
						? this.servicioRepository.save(vendible)
						: productoRepository.save(vendible);
				if (addedVendible != null) {
					String addVendibleUrl = microServicioUsuarioUrl
							+ UsersControllerUrls.PROVEEDOR_VENDIBLE.replace("{proveedorId}", proveedorId.toString())
									.replace("{vendibleId}", addedVendible.getId().toString());

					ResponseEntity<Void> addVendibleResponse = restTemplate.exchange(addVendibleUrl, HttpMethod.PATCH,
							null, Void.class);

					return addVendibleResponse.getStatusCodeValue() == 200 ? addedVendible : null;

				}
				throw new Exception("Unknown error");
			} else {
				throw new UserNotFoundException();
			}
		} catch (Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				throw new VendibleAlreadyExistsException();
			} else {
				throw e;
			}
		}
	}

	@Transactional
	public Vendible update(VendibleDTO vendible, Long vendibleId, String concreteVendibleDTOClass,
			String entityFullClassName, String vendibleType) throws Exception {
		Optional<Vendible> toUpdateVendibleOpt = vendibleRepository.findById(vendibleId);
		if (toUpdateVendibleOpt.isPresent()) {
			String vendibleRealType = this.getVendibleTypeById(vendibleId);
			
			if (!vendibleRealType.equalsIgnoreCase(vendibleType)) {
				throw new VendibleNotFoundException();
			}
			
			Vendible toUpdateVendible = toUpdateVendibleOpt.get();

			try {
				ReflectionHelper.applySetterFromExistingFields(vendible, toUpdateVendible, concreteVendibleDTOClass,
						entityFullClassName);

				Vendible updatedVendible = vendibleType.equals(VendibleType.SERVICIO.toString())
						? servicioRepository.save(toUpdateVendible)
						: productoRepository.save(toUpdateVendible);

				return updatedVendible;

			} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException
					| InvocationTargetException e) {
				throw new Exception("Reflection exception throwed");
			}
		}

		throw new VendibleNotFoundException();
	}

	public void deleteById(Long id) throws VendibleNotFoundException {
		try {
			vendibleRepository.deleteAllProvedoresAndVendiblesRelations(id);
			vendibleRepository.deleteById(id);
		} catch (EmptyResultDataAccessException ex) {
			throw new VendibleNotFoundException();
		}
	}
	
	public Optional<Vendible> findById(Long VendibleId) {
		return this.vendibleRepository.findById(VendibleId);
	}
	
	public String getVendibleTypeById(Long vendibleId) {
		Optional<Vendible> vendibleOpt = findById(vendibleId);
		return vendibleOpt.isPresent() ? vendibleRepository.getVendibleTypeById(vendibleId) : "";
	}
}
