package com.contractar.microserviciousuario.sorting;

import com.contractar.microserviciocommons.dto.proveedorvendible.AbstractProveedorVendibleDTO;
import com.contractar.microserviciousuario.dtos.DistanceProveedorDTO;

public class VendibleDistanceComparator extends BaseComparator {

	public VendibleDistanceComparator() {}

	@Override
	public int compare(AbstractProveedorVendibleDTO o1, AbstractProveedorVendibleDTO o2) {
		DistanceProveedorDTO first = (DistanceProveedorDTO) o1;
		
		DistanceProveedorDTO second = (DistanceProveedorDTO) o2;
		
		boolean isLower = first.getDistance() < second.getDistance();

		boolean isEqual = first.getDistance() == second.getDistance();

		return isLower ? -1 : isEqual ? 0 : 1;
	}

}
