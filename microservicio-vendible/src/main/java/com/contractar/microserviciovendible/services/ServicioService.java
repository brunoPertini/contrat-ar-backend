package com.contractar.microserviciovendible.services;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.repository.ServicioRepository;

@Service
public class ServicioService {
	@Autowired
	private ServicioRepository servicioRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private String getUsuarioUrl = "http://localhost:8002/usuarios/proveedor?id={id}";
	
	public Servicio save(Servicio servicio, Long proveedorId) throws Exception {
		try {
			Map<String, Long> parameters = Map.ofEntries(new AbstractMap.SimpleEntry<String, Long>("id", proveedorId));
			Proveedor proveedor = restTemplate.getForObject(getUsuarioUrl, Proveedor.class, parameters);
			if (proveedor != null) {
				Servicio addedServicio = this.servicioRepository.save(servicio);
				proveedor.getVendibles().add(addedServicio);
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
