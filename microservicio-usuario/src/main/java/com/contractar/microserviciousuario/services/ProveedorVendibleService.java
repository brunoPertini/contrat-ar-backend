package com.contractar.microserviciousuario.services;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendibleUpdateDTO;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.VendibleUpdateException;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;
import com.contractar.microserviciousuario.repository.ProveedorVendibleRepository;
import com.contractar.microserviciovendible.models.Vendible;

@Service
public class ProveedorVendibleService {
	@Autowired
	private ProveedorVendibleRepository repository;

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
}
