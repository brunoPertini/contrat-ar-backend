package com.contractar.microserviciovendible.services;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.dto.ServicioDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
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
			String usuarioExistsUrl = microServicioUsuarioUrl + 
					UsersControllerUrls.USUARIO_EXISTS.replace("{usuarioId}", proveedorId.toString());

			ResponseEntity<Void> getUsuarioResponse = restTemplate.getForEntity(usuarioExistsUrl, null, ResponseEntity.class); 
			
			if (getUsuarioResponse.getStatusCode().is2xxSuccessful()) {
				Servicio addedServicio = this.servicioRepository.save(servicio);
				if (addedServicio != null) {
					String addVendibleUrl = 
							microServicioUsuarioUrl +
							UsersControllerUrls.PROVEEDOR_VENDIBLE.replace(
							"{proveedorId}",
							proveedorId.toString())
							.replace("{vendibleId}",
							servicio.getId().toString());
					
										
					ResponseEntity<Void> addVendibleResponse = restTemplate.exchange(
					        addVendibleUrl, HttpMethod.PATCH, null, Void.class);
					
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
	public ServicioDTO update (ServicioDTO servicio, Long vendibleId) throws VendibleNotFoundException,
	ClassNotFoundException,
	IllegalArgumentException,
	IllegalAccessException, InvocationTargetException {
		Optional<Vendible> toUpdateServiceOpt = vendibleRepository.findById(vendibleId);
		if (toUpdateServiceOpt.isPresent()) {
			Vendible toUpdateService = toUpdateServiceOpt.get();
			
			Class<?> dtoClass = Class.forName("com.contractar.microserviciocommons.dto.ServicioDTO");
			Class<?> vendibleClass = Class.forName("com.contractar.microserviciovendible.models.Vendible");

			LinkedHashMap<String, Object> notEmptyFields = new LinkedHashMap<String, Object>();
			Field [] fields = dtoClass.getDeclaredFields();
			
			for(Field field: fields) {
				field.setAccessible(true);
				Object fieldValue = field.get(servicio);
				if ((fieldValue instanceof Number && (((Number)fieldValue).intValue() > 0)) || fieldValue != null) {
					notEmptyFields.put(field.getName(), fieldValue);
				}
			}
			
			Method[] methods = vendibleClass.getMethods();
			
			
			for (String fieldName: notEmptyFields.keySet()) {
				String sufix =  Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
				String setterExpectedName = "set"+ sufix;
				
				Method foundSetterMethod = null;

				while (foundSetterMethod == null) {
					for(Method method: methods) {
						if (method.getName().equals(setterExpectedName)) {
							foundSetterMethod = method;
						}
					}
				}
				
				foundSetterMethod.invoke(toUpdateService, notEmptyFields.get(fieldName));
			}
			
			Servicio updatedServicio = servicioRepository.save((Servicio)toUpdateService);
			
			return new ServicioDTO(updatedServicio.getNombre(),
					updatedServicio.getPrecio(),
					updatedServicio.getDescripcion(),
					updatedServicio.getImagesUrl(),
					updatedServicio.getProveedores());		
		}
		
		throw new VendibleNotFoundException();
	}
	
	public List<ServicioDTO> findByNombreAsc(String nombre) {
		try {
			return this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre)
					.stream()
					.map(servicio -> new ServicioDTO(servicio.getNombre(),
							servicio.getPrecio(),
							servicio.getDescripcion(),
							servicio.getImagesUrl(),
							servicio.getProveedores()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw e;
		}
	}
}
