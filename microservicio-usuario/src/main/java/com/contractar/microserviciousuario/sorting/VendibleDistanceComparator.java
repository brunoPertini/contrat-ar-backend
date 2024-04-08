package com.contractar.microserviciousuario.sorting;

import java.util.Comparator;

import com.contractar.microservicioadapter.dtos.AbstractProveedorVendibleDTOAccesor;
import com.contractar.microserviciousuario.dtos.DistanceProveedorDTO;

public class VendibleDistanceComparator implements Comparator<AbstractProveedorVendibleDTOAccesor> {

	public VendibleDistanceComparator() {
	}

	@Override
	public int compare(AbstractProveedorVendibleDTOAccesor first, AbstractProveedorVendibleDTOAccesor second) {
		DistanceProveedorDTO firstDistanceProveedorDTO = (DistanceProveedorDTO) first;
		DistanceProveedorDTO secondDistanceProveedorDTO = (DistanceProveedorDTO) first;
		
		boolean isLower = firstDistanceProveedorDTO.getDistance() < secondDistanceProveedorDTO.getDistance();

		boolean isEqual = firstDistanceProveedorDTO.getDistance() == secondDistanceProveedorDTO.getDistance();

		return isLower ? -1 : isEqual ? 0 : 1;
	}

}
