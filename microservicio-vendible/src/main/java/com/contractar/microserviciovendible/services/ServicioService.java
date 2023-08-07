package com.contractar.microserviciovendible.services;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.dto.ServicioDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleUpdateException;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciovendible.repository.ServicioRepository;
import com.contractar.microserviciovendible.repository.VendibleRepository;

import jakarta.transaction.Transactional;

@Service
public class ServicioService {
	@Autowired
	private ServicioRepository servicioRepository;

	@Autowired
	private VendibleRepository vendibleRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${microservicio-usuario.url}")
	private String microServicioUsuarioUrl;

	public Servicio save(Servicio servicio, Long proveedorId) throws Exception {
		try {
			String usuarioExistsUrl = microServicioUsuarioUrl
					+ UsersControllerUrls.USUARIO_EXISTS.replace("{usuarioId}", proveedorId.toString());

			ResponseEntity<Void> getUsuarioResponse = restTemplate.getForEntity(usuarioExistsUrl, null,
					ResponseEntity.class);

			if (getUsuarioResponse.getStatusCode().is2xxSuccessful()) {
				Servicio addedServicio = this.servicioRepository.save(servicio);
				if (addedServicio != null) {
					String addVendibleUrl = microServicioUsuarioUrl
							+ UsersControllerUrls.PROVEEDOR_VENDIBLE.replace("{proveedorId}", proveedorId.toString())
									.replace("{vendibleId}", servicio.getId().toString());

					ResponseEntity<Void> addVendibleResponse = restTemplate.exchange(addVendibleUrl, HttpMethod.PATCH,
							null, Void.class);

					return addVendibleResponse.getStatusCodeValue() == 200 ? addedServicio : null;

				}
				return addedServicio;
			} else {
				throw new UserNotFoundException();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public ServicioDTO update(ServicioDTO servicio, Long vendibleId) throws Exception {
		Optional<Vendible> toUpdateServiceOpt = vendibleRepository.findById(vendibleId);
		if (toUpdateServiceOpt.isPresent()) {
			Vendible toUpdateService = toUpdateServiceOpt.get();

			try {
				ReflectionHelper.applySetterFromExistingFields(servicio, toUpdateService,
						"com.contractar.microserviciocommons.dto.ServicioDTO",
						"com.contractar.microserviciovendible.models.Vendible");
				Servicio updatedServicio = servicioRepository.save((Servicio) toUpdateService);

				return new ServicioDTO(updatedServicio.getNombre(), updatedServicio.getPrecio(),
						updatedServicio.getDescripcion(), updatedServicio.getImagesUrl(),
						updatedServicio.getProveedores());
			} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException
					| InvocationTargetException e) {
				throw new Exception("Reflection exception throwed");
			}
		}

		throw new VendibleNotFoundException();
	}

	public List<ServicioDTO> findByNombreAsc(String nombre) {
		try {
			return this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream()
					.map(servicio -> new ServicioDTO(servicio.getNombre(), servicio.getPrecio(),
							servicio.getDescripcion(), servicio.getImagesUrl(), servicio.getProveedores()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw e;
		}
	}
}
