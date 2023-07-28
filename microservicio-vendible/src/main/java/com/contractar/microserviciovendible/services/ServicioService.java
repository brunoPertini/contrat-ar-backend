package com.contractar.microserviciovendible.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.repository.ServicioRepository;

@Service
public class ServicioService {
	@Autowired
	private ServicioRepository servicioRepository;
	
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
	
	public List<Servicio> findByNombreAsc(String nombre) {
		return this.servicioRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre);
	}
}
